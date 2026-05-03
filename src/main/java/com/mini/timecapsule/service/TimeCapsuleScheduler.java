package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.QUser;
import com.mini.timecapsule.model.User;
import com.mini.timecapsule.model.User.OpenDay;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TimeCapsuleScheduler {

    private final UserRepository userRepository;
    private final LetterPaperRepository letterPaperRepository;
    private final SendEmailUtils sendEmailUtils;

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void processTimeCapsules() {
        log.info("Running TimeCapsule scheduler...");

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        OpenDay currentOpenDay = null;
        if (month == 1 && day == 1) {
            currentOpenDay = OpenDay.NEW_YEAR;
        } else if (month == 2 && day == 28) {
            currentOpenDay = OpenDay.SPRING_BREAK;
        } else if (month == 12 && day == 31) {
            currentOpenDay = OpenDay.END_YEAR;
        }

        if (currentOpenDay == null) {
            log.info("Today is not an OpenDay. Exiting scheduler.");
            return;
        }

        log.info("Today is an OpenDay: " + currentOpenDay.getValue());

        BooleanBuilder predicate = new BooleanBuilder();
        QUser qUser = QUser.user;
        predicate.and(qUser.openDayType.eq(currentOpenDay));

        Iterable<User> users = userRepository.findAll(predicate);

        for (User user : users) {
            List<LetterPaper> letters = letterPaperRepository.findByUser(user);
            List<LetterPaper> unopenedLetters = letters.stream()
                .filter(l -> l.getStatus() == LetterPaper.LetterStatus.UNOPENED)
                .collect(Collectors.toList());

            if (!unopenedLetters.isEmpty()) {
                StringBuilder emailText = new StringBuilder();
                emailText.append("당신의 타임캡슐이 도착했습니다!\n\n");

                for (LetterPaper letter : unopenedLetters) {
                    emailText.append("보낸 사람: ").append(letter.getName()).append("\n");
                    emailText.append("내용: \n").append(letter.getContent()).append("\n");
                    emailText.append("--------------------------------------------------\n");

                    letter.read(); // Marks as OPENED
                    letterPaperRepository.save(letter);
                }

                log.info("Sending email to: " + user.getEmail() + " with " + unopenedLetters.size() + " letters.");
                try {
                    sendEmailUtils.sendSimpleMessage(user.getEmail(), "타임캡슐이 오픈되었습니다!", emailText.toString());
                } catch (Exception e) {
                    log.error("Failed to send email to " + user.getEmail(), e);
                }
            }
        }

        log.info("Finished TimeCapsule scheduler.");
    }
}
