package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimecapsuleScheduler {

    private final LetterPaperRepository letterPaperRepository;
    private final CapsuleNotifier notifier;

    @Value("${timecapsule.notification.max-attempts:10}")
    private int maxAttempts;

    /**
     * 한 주기에 처리할 최대 건수.
     *
     * <p>기본 100 의 근거(운영 cron 은 매분 — k8s deployment.yaml):
     * <ul>
     *   <li>정상 SMTP 왕복은 건당 대략 0.2~0.5초다. 100건이면 최악 50초로 다음 tick(60초)
     *       안에 끝난다. 스케줄러 스레드는 하나뿐이라 tick 이 밀리면 그대로 지연이 된다.</li>
     *   <li>분당 100건 = 시간당 6,000건. 수천 건 적체도 몇 분 안에 빠진다.</li>
     *   <li>수신 MTA 입장에서 초당 1.7건이라 급증 발송으로 차단당할 수준이 아니다.</li>
     * </ul>
     */
    @Value("${timecapsule.notification.batch-size:100}")
    private int batchSize;

    /**
     * 켜고 기동하면 재시도 예산이 소진된 캡슐을 대기열로 되돌린다.
     * 상시로 켜두면 안 된다 — 진짜로 배달 불가능한 주소가 매 재기동마다 되살아난다.
     */
    @Value("${timecapsule.notification.requeue-exhausted-on-start:false}")
    private boolean requeueExhaustedOnStart;

    /**
     * 기동할 때 "영영 메일을 못 받는 캡슐"이 몇 건인지 알린다.
     *
     * <p>{@code notifyAttempts} 가 상한에 닿은 캡슐은 대기열 쿼리에서 영구히 빠지고,
     * 그걸 되살릴 관리자 화면은 인증 없이 JWT 를 뿌리던 문제로 통째로 제거됐다.
     * 아무도 세지 않으면 조용한 데이터 유실로 끝난다.
     *
     * <p>{@code @Transactional} 을 붙이지 않는다 — 이 클래스는 트랜잭션 경계를 갖지 않는
     * 오케스트레이터다. 쓰기는 {@link CapsuleNotifier} 안에서 일어난다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void reviewUndeliverableCapsulesOnStartup() {
        if (requeueExhaustedOnStart) {
            int requeued = notifier.requeueExhausted(maxAttempts);
            log.warn("재시도 예산이 소진됐던 캡슐 {}건을 대기열로 되돌립니다. "
                    + "원인(대개 메일 설정)을 고친 뒤에만 쓸 것 — 켜둔 채로 두지 말 것", requeued);
            return;
        }

        long undeliverable = letterPaperRepository.countExhausted(LetterPaper.LetterStatus.REMOVE, maxAttempts);
        if (undeliverable > 0) {
            log.error("재시도 {}회를 모두 소진해 영구 미발송 상태인 캡슐이 {}건 있습니다. "
                            + "메일 설정을 고친 뒤 TIMECAPSULE_NOTIFICATION_REQUEUE_EXHAUSTED_ON_START=true 로 "
                            + "한 번 기동하면 대기열로 되돌아갑니다",
                    maxAttempts, undeliverable);
        }
    }

    /**
     * 개봉일이 지난 캡슐에 알림 메일을 보낸다. 코드 기본값은 매시 정각, 운영은 매분.
     *
     * <p>이 메서드에는 {@code @Transactional} 을 붙이지 않는다 — 붙이면 배치 전체가
     * 한 트랜잭션이 되어, 중간에 한 건이 터질 때 이미 발송한 메일들의 발송완료 표시까지
     * 롤백된다(= 다음 주기에 전원 중복 발송). 커밋 단위는 {@link CapsuleNotifier} 가
     * 캡슐 한 건으로 잡는다.
     */
    @Scheduled(cron = "${timecapsule.notification.cron:0 0 * * * *}")
    public void checkAndNotifyOpenCapsules() {
        if (!notifier.isMailConfigured()) {
            // MAIL_HOST 시크릿 없이 매니페스트만 적용하면 localhost:25 로 떨어진다
            // (deployment.yaml 의 secretKeyRef ... optional: true). 이때 발송을 시도하면
            // 전부 즉시 실패하고 그 실패가 캡슐의 재시도 예산을 먹는다. 매분 주기에서는
            // 10분 만에 그 사이 개봉일이 된 캡슐이 전부 영구 미발송이 된다.
            // 조용히 넘기지 않고 매 주기 ERROR 를 남긴다 — health 의 mail 인디케이터는
            // 일부러 꺼져 있어서(메일 장애 = 사이트 다운 방지) 이 로그가 주 관측 수단이다.
            log.error("메일이 설정되지 않아 개봉 알림 주기를 건너뜁니다. MAIL_HOST 시크릿을 확인하세요 "
                    + "(상태는 /actuator/health/mail)");
            return;
        }

        List<Long> dueIds = letterPaperRepository.findIdsDueForNotification(
                ZonedDateTime.now(), LetterPaper.LetterStatus.REMOVE, maxAttempts, Pageable.ofSize(batchSize));

        if (dueIds.isEmpty()) {
            return;
        }
        log.info("개봉 알림 대상 캡슐 {}건", dueIds.size());
        if (dueIds.size() >= batchSize) {
            // 조용히 자르면 "대상 100건을 다 보냈다"로 읽힌다. 실제로는 남은 캡슐이 있고,
            // 알림 지연을 조사할 때 이 줄이 유일한 단서다. 처리 전에 남긴다 — 발송 중
            // tick 이 길어져도(전건 타임아웃 등) 잘렸다는 사실은 먼저 보여야 한다.
            log.warn("이번 주기는 상한 {}건에서 잘렸습니다 — 남은 대상은 다음 주기에 처리됩니다", batchSize);
        }

        for (Long capsuleId : dueIds) {
            try {
                notifier.notifyOne(capsuleId, maxAttempts);
            } catch (Exception e) {
                // notifyOne 내부에서 메일 실패는 이미 삼킨다. 여기까지 올라오는 건
                // 커밋 실패 같은 트랜잭션 레벨 문제 — 한 건 때문에 배치를 멈추지 않는다.
                log.error("개봉 알림 처리 실패: capsuleId={}", capsuleId, e);
            }
        }
    }
}
