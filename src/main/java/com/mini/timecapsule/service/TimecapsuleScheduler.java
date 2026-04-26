package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimecapsuleScheduler {

    private final LetterPaperRepository letterPaperRepository;
    private final SendEmailUtils sendEmailUtils;

    /**
     * 매 정각마다 개봉 예정인 타임캡슐을 확인하여 사용자에게 알림을 보냅니다.
     * (cron = "0 0 * * * *")
     * 테스트를 위해 1분마다 실행되도록 설정할 수 있습니다.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void checkAndNotifyOpenCapsules() {
        log.info("Checking for capsules to open at {}", ZonedDateTime.now());

        List<LetterPaper> dueCapsules = letterPaperRepository.findAllByOpenAtBeforeAndIsNotifiedFalseAndStatus(
                ZonedDateTime.now(),
                LetterPaper.LetterStatus.UNOPENED
        );

        for (LetterPaper capsule : dueCapsules) {
            try {
                String userEmail = capsule.getUser().getEmail();
                if (userEmail != null && !userEmail.isEmpty()) {
                    sendEmailUtils.sendNotificationEmail(userEmail, capsule.getName());
                    capsule.setIsNotified(true);
                    log.info("Notification sent to {} for capsule {}", userEmail, capsule.getId());
                }
            } catch (Exception e) {
                log.error("Failed to send notification for capsule {}: {}", capsule.getId(), e.getMessage());
            }
        }
    }
}
