package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 캡슐 <b>한 건</b>의 개봉 알림 발송. 트랜잭션 경계가 한 건이라는 것이 이 클래스의 존재 이유다.
 *
 * <p>예전에는 스케줄러 메서드 전체가 하나의 트랜잭션이었다. 앞의 50건은 메일이 이미
 * 나갔는데 51번째에서 예외가 나면 {@code isNotified} 가 통째로 롤백돼서 다음 주기에
 * 50명이 같은 메일을 또 받았다. 지금은 중복 발송 반경이 1건이다.
 *
 * <p>여전히 at-least-once 다 — 메일을 보낸 뒤 커밋이 실패하면 그 한 건은 재발송된다.
 * 반대로 "먼저 발송완료로 커밋하고 메일을 보내는" 순서로 바꾸면 프로세스가 그 사이에
 * 죽었을 때 메일이 영영 안 나간다. 타임캡슐에서는 중복이 유실보다 낫다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CapsuleNotifier {

    private final LetterPaperRepository letterPaperRepository;
    private final CapsuleNotificationMailer mailer;

    /** 지금 메일이 실제로 나갈 수 있는 배선인지. 스케줄러가 주기 진입 전에 확인한다. */
    public boolean isMailConfigured() {
        return mailer.isConfigured();
    }

    /**
     * 재시도 예산이 소진돼 대기열에서 빠진 캡슐을 되돌린다. 되돌린 건수를 반환한다.
     *
     * <p>설정 오류로 예산이 타버린 캡슐을 살리는 유일한 경로다. HTTP 로 열지 않은 이유는
     * 이 서비스가 비회원 전용이라 인증된 관리자 표면이 아예 없기 때문이다 — 되살리기
     * API 를 열면 아무나 호출해 전체 재발송을 유발할 수 있다. 호출은
     * {@code TimecapsuleScheduler} 의 기동 훅이 한다.
     */
    @Transactional
    public int requeueExhausted(int maxAttempts) {
        return letterPaperRepository.resetExhaustedNotifyAttempts(LetterPaper.LetterStatus.REMOVE, maxAttempts);
    }

    /**
     * 이 실패가 "캡슐의 문제"가 아니라 "메일 인프라의 문제"인가.
     *
     * <p>인프라 장애는 그 시각의 모든 캡슐에 똑같이 일어난다. 여기서 재시도 예산을 깎으면
     * 장애 시간 × 주기만큼의 캡슐이 통째로 소각된다 — 운영 cron 이 매분이라
     * SMTP 가 10분만 죽어도 그 사이 개봉일이 된 캡슐은 전부 영구 미발송이 된다.
     *
     * <p>판별 근거는 JavaMailSenderImpl 이 실제로 던지는 모양이다.
     * <ul>
     *   <li>인증 실패는 {@code MailAuthenticationException} — 명백한 배포 설정 문제다.</li>
     *   <li>연결 실패는 {@code MailSendException("Mail server connection failed", ex, failedMessages)}
     *       인데, <b>failedMessages 가 비어 있지 않다</b>. 남은 메시지 전부를 같은 연결
     *       예외에 매핑해 넣기 때문이다("Effectively, all remaining messages failed").
     *       그래서 "failedMessages 가 비었는가"로는 구분할 수 없다 — 실제로 그렇게 짰다가
     *       소켓 테스트에서 걸렸다.</li>
     *   <li>개별 메시지 거절은 {@code MailSendException(failedMessages)} 라 <b>cause 가 null</b>
     *       이고, 실패 사유는 메시지별로 다르다.</li>
     * </ul>
     * 따라서 기준은 "cause 가 있고, 실패한 메시지들이 전부 그 하나의 원인으로 실패했는가" 다.
     * 이 가정은 {@code CapsuleNotifierTest} 가 진짜 소켓을 물려 고정한다.
     */
    static boolean isMailInfrastructureFailure(Exception e) {
        if (e instanceof MailAuthenticationException) {
            return true;
        }
        if (!(e instanceof MailSendException sendException)) {
            return false;
        }
        Throwable cause = sendException.getCause();
        return cause != null
                && sendException.getFailedMessages().values().stream().allMatch(failure -> failure == cause);
    }

    @Transactional
    public void notifyOne(Long capsuleId, int maxAttempts) {
        if (!mailer.isConfigured()) {
            // 스케줄러가 이미 걸러내지만, 재시도 예산을 깎는 유일한 지점이 여기라
            // 불변식도 여기서 지킨다. 설정 오류가 캡슐을 영구 미발송으로 만들면 안 된다.
            log.error("메일이 설정되지 않아 발송을 건너뜁니다(시도횟수 유지): capsuleId={}", capsuleId);
            return;
        }

        Optional<LetterPaper> found = letterPaperRepository.findById(capsuleId);
        if (found.isEmpty()) {
            // 대상 조회와 발송 사이에 지워졌다. 경합이지 오류가 아니다.
            log.warn("알림 대상 캡슐이 사라졌습니다: capsuleId={}", capsuleId);
            return;
        }
        LetterPaper capsule = found.get();

        if (!StringUtils.hasText(capsule.getReceiverEmail())) {
            log.warn("수신 이메일이 없어 알림을 건너뜁니다: capsuleId={}", capsuleId);
            capsule.markNotified();
            return;
        }

        try {
            mailer.send(capsule);
            capsule.markNotified();
            log.info("개봉 알림 발송 완료: capsuleId={}", capsuleId);
        } catch (Exception e) {
            if (isMailInfrastructureFailure(e)) {
                // 메일 서버 자체에 닿지 못했다 — 이 캡슐의 문제가 아니므로 예산을 깎지 않는다.
                // 결과적으로 인프라가 복구될 때까지 무제한 재시도가 되는데, 이 클래스가
                // 이미 선언한 트레이드오프("중복이 유실보다 낫다")와 같은 방향이다.
                // 대기열은 batch-size 로 묶여 있고 매 주기 이 ERROR 가 남는다.
                log.error("메일 서버에 닿지 못해 발송에 실패했습니다(시도횟수 유지): capsuleId={}", capsuleId, e);
                return;
            }
            boolean giveUp = capsule.recordNotifyFailure(maxAttempts);
            if (giveUp) {
                log.error("개봉 알림 발송 {}회 실패 — 재시도를 중단합니다: capsuleId={}",
                        maxAttempts, capsuleId, e);
            } else {
                log.error("개봉 알림 발송 실패({}/{}) — 다음 주기에 재시도합니다: capsuleId={}",
                        capsule.getNotifyAttempts(), maxAttempts, capsuleId, e);
            }
            // 예외를 다시 던지지 않는다 — 던지면 실패 횟수 증가분까지 롤백돼서
            // max-attempts 상한이 영원히 도달하지 않는다.
        }
    }
}
