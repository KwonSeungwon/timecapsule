package com.mini.timecapsule.service;

import com.mini.timecapsule.model.LetterPaper;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 개봉일이 된 타임캡슐의 알림 메일을 조립하고 보낸다.
 *
 * 발송 실패는 삼키지 않는다 — 예외를 그대로 올려 호출자(스케줄러)가
 * 재시도 횟수를 기록하게 한다. 예전엔 여기서 catch 로 삼켜서
 * "메일이 안 나가는데 로그만 조용한" 상태가 오래 방치됐다.
 */
@Slf4j
@Component
public class CapsuleNotificationMailer {

    private static final Map<String, String> BIOME_EMOJI = Map.of(
            "BEACH", "🌊", "MOUNTAIN", "⛰️", "DESERT", "🏜️", "FOREST", "🌲");

    private static final String SUBJECT_PREFIX = "[TimeCapsule] 캡슐이 열렸습니다 — ";
    private static final String UNTITLED = "제목 없는 캡슐";

    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine;
    private final String baseUrl;
    private final String fromAddress;

    public CapsuleNotificationMailer(JavaMailSender mailSender,
                                     ITemplateEngine templateEngine,
                                     @Value("${timecapsule.notification.base-url}") String baseUrl,
                                     @Value("${timecapsule.notification.from:}") String fromAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.fromAddress = fromAddress;
    }

    /**
     * 부팅 시 메일 모드를 한 줄로 크게 남긴다.
     * in-cluster sink 는 SMTP 왕복까지는 진짜로 동작하지만 실제 수신함엔 도달하지 않는다.
     * 이걸 로그에 남기지 않으면 "정상 발송"으로 오독된다.
     */
    @PostConstruct
    void logMailMode() {
        if (mailSender instanceof JavaMailSenderImpl impl) {
            log.warn("=== TimeCapsule mail mode: {} ===", describeMailMode(impl.getHost(), impl.getPort()));
        }
    }

    static String describeMailMode(String host, int port) {
        if (host == null || host.isBlank() || "localhost".equals(host)) {
            return "UNCONFIGURED (host=" + host + ":" + port + ") — 메일이 발송되지 않습니다";
        }
        if (host.contains("mailpit") || host.contains("mail-sink")) {
            return "SINK (" + host + ":" + port + ") — SMTP 경로는 동작하지만 실제 수신함으로는 가지 않습니다";
        }
        return "SMTP (" + host + ":" + port + ")";
    }

    /** 메일 본문(HTML)만 렌더링한다. 테스트에서 본문 검증용으로도 쓴다. */
    public String renderBody(LetterPaper capsule) {
        Context ctx = new Context();
        ctx.setVariable("senderName", orDefault(capsule.getSenderName(), "누군가"));
        ctx.setVariable("title", orDefault(capsule.getTitle(), UNTITLED));
        ctx.setVariable("locationName", orDefault(capsule.getLocationName(), "알 수 없는 곳"));
        ctx.setVariable("biomeEmoji", BIOME_EMOJI.getOrDefault(capsule.getBiome(), "📍"));
        ctx.setVariable("xCoord", capsule.getXCoord());
        ctx.setVariable("yCoord", capsule.getYCoord());
        ctx.setVariable("capsuleUrl", capsuleUrl(capsule.getReceiverEmail()));
        return templateEngine.process("mail/capsule-opened", ctx);
    }

    private static String orDefault(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    /**
     * URLEncoder 는 application/x-www-form-urlencoded 규칙이라 '@' → %40, '+' → %2B 로
     * 이미 인코딩한다(안전 문자는 영숫자와 . - * _ 뿐). 쿼리스트링 값으로 그대로 쓸 수 있다.
     */
    private String capsuleUrl(String email) {
        return baseUrl + "/my-capsule?email="
                + URLEncoder.encode(email == null ? "" : email, StandardCharsets.UTF_8);
    }

    /** 발송 직전의 MimeMessage 를 조립한다. */
    public MimeMessage compose(LetterPaper capsule) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            if (StringUtils.hasText(fromAddress)) {
                helper.setFrom(fromAddress);
            }
            helper.setTo(capsule.getReceiverEmail());
            helper.setSubject(SUBJECT_PREFIX + orDefault(capsule.getTitle(), UNTITLED));
            helper.setText(renderBody(capsule), true);
        } catch (jakarta.mail.MessagingException e) {
            throw new IllegalStateException("알림 메일 조립 실패: capsuleId=" + capsule.getId(), e);
        }
        return message;
    }

    /** 발송. 실패하면 예외가 그대로 올라간다 — 절대 삼키지 않는다. */
    public void send(LetterPaper capsule) {
        mailSender.send(compose(capsule));
    }
}
