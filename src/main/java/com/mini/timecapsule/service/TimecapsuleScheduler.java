package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;

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
                String receiverEmail = capsule.getReceiverEmail();
                if (receiverEmail != null && !receiverEmail.isEmpty()) {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(receiverEmail);
                    message.setSubject("[TimeCapsule] 당신에게 도착한 비밀 편지가 있습니다.");
                    message.setText("당신에게 캡슐이 도착했습니다. 확인해 보세요! 링크: http://timecapsule.chatflow.ai.kr/my-capsule?email=" + receiverEmail);
                    mailSender.send(message);

                    capsule.setIsNotified(true);
                    letterPaperRepository.save(capsule);
                    log.info("Notification sent to {} for capsule {}", receiverEmail, capsule.getId());
                }
            } catch (Exception e) {
                log.error("Failed to send notification for capsule {}: {}", capsule.getId(), e.getMessage());
            }
        }
    }
}
