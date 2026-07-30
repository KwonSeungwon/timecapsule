package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimecapsuleScheduler {

    private final LetterPaperRepository letterPaperRepository;
    private final CapsuleNotificationMailer mailer;

    @Value("${timecapsule.notification.max-attempts:10}")
    private int maxAttempts;

    /**
     * 개봉일이 지난 캡슐에 알림 메일을 보낸다.
     * 기본 매시 정각. 실패한 캡슐은 isNotified 가 false 로 남아 다음 주기에 재시도되고,
     * max-attempts 회 실패하면 쿼리 대상에서 빠진다(무한 재시도 방지).
     */
    @Scheduled(cron = "${timecapsule.notification.cron:0 0 * * * *}")
    @Transactional
    public void checkAndNotifyOpenCapsules() {
        List<LetterPaper> dueCapsules = letterPaperRepository.findDueForNotification(
                ZonedDateTime.now(), LetterPaper.LetterStatus.UNOPENED, maxAttempts);

        if (dueCapsules.isEmpty()) {
            return;
        }
        log.info("개봉 알림 대상 캡슐 {}건", dueCapsules.size());

        for (LetterPaper capsule : dueCapsules) {
            if (!StringUtils.hasText(capsule.getReceiverEmail())) {
                log.warn("수신 이메일이 없어 알림을 건너뜁니다: capsuleId={}", capsule.getId());
                capsule.markNotified();
                continue;
            }
            try {
                mailer.send(capsule);
                capsule.markNotified();
                log.info("개봉 알림 발송 완료: capsuleId={}", capsule.getId());
            } catch (Exception e) {
                boolean giveUp = capsule.recordNotifyFailure(maxAttempts);
                if (giveUp) {
                    log.error("개봉 알림 발송 {}회 실패 — 재시도를 중단합니다: capsuleId={}",
                            maxAttempts, capsule.getId(), e);
                } else {
                    log.error("개봉 알림 발송 실패({}/{}) — 다음 주기에 재시도합니다: capsuleId={}",
                            capsule.getNotifyAttempts(), maxAttempts, capsule.getId(), e);
                }
            }
        }
        letterPaperRepository.saveAll(dueCapsules);
    }
}
