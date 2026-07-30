package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
     * 개봉일이 지난 캡슐에 알림 메일을 보낸다. 기본 매시 정각.
     *
     * <p>이 메서드에는 {@code @Transactional} 을 붙이지 않는다 — 붙이면 배치 전체가
     * 한 트랜잭션이 되어, 중간에 한 건이 터질 때 이미 발송한 메일들의 발송완료 표시까지
     * 롤백된다(= 다음 주기에 전원 중복 발송). 커밋 단위는 {@link CapsuleNotifier} 가
     * 캡슐 한 건으로 잡는다.
     */
    @Scheduled(cron = "${timecapsule.notification.cron:0 0 * * * *}")
    public void checkAndNotifyOpenCapsules() {
        List<Long> dueIds = letterPaperRepository.findIdsDueForNotification(
                ZonedDateTime.now(), LetterPaper.LetterStatus.REMOVE, maxAttempts);

        if (dueIds.isEmpty()) {
            return;
        }
        log.info("개봉 알림 대상 캡슐 {}건", dueIds.size());

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
