package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public void notifyOne(Long capsuleId, int maxAttempts) {
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
