package com.mini.timecapsule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendEmailUtils {

    private final JavaMailSender mailSender;

    public void sendNotificationEmail(String to, String capsuleName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[타임캡슐] 당신의 타임캡슐이 도착했습니다!");
        message.setText("안녕하세요!\n\n당신이 묻어두었던 타임캡슐 '" + capsuleName + "'이(가) 드디어 개봉 가능한 시간이 되었습니다.\n" +
                "지금 바로 웹사이트에 접속하여 확인해보세요!\n\n" +
                "감사합니다.");
        
        mailSender.send(message);
    }
}
