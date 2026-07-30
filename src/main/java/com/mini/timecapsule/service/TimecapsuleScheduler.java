package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
     * 개봉일이 지난 캡슐에 알림 메일을 보낸다. 코드 기본값은 매시 정각, 운영은 매분.
     *
     * <p>이 메서드에는 {@code @Transactional} 을 붙이지 않는다 — 붙이면 배치 전체가
     * 한 트랜잭션이 되어, 중간에 한 건이 터질 때 이미 발송한 메일들의 발송완료 표시까지
     * 롤백된다(= 다음 주기에 전원 중복 발송). 커밋 단위는 {@link CapsuleNotifier} 가
     * 캡슐 한 건으로 잡는다.
     */
    @Scheduled(cron = "${timecapsule.notification.cron:0 0 * * * *}")
    public void checkAndNotifyOpenCapsules() {
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
