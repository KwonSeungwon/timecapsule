package com.mini.timecapsule.service;

import com.mini.timecapsule.model.LetterPaper;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
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
 *
 * <p>{@link HealthIndicator} 를 겸한다. 메일이 실제로 설정돼 있는지는 이 클래스만
 * 판단할 수 있고, 운영자가 그 상태를 확인할 수 있어야 하기 때문이다. 자세한 계약은
 * {@link #health()} 주석 참고.
 */
@Slf4j
@Component
public class CapsuleNotificationMailer implements HealthIndicator {

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

    /** 메일 배선의 실제 상태. 이 값에 따라 재시도 예산을 깎을지 말지가 갈린다. */
    public enum MailMode {
        /** MAIL_HOST 가 없어 localhost 로 떨어진 상태. 발송은 전부 즉시 실패한다. */
        UNCONFIGURED,
        /** 클러스터 내부 sink. SMTP 왕복은 성공하지만 실제 수신함엔 도달하지 않는다. */
        SINK,
        /** 진짜 SMTP 릴레이. */
        SMTP
    }

    /**
     * 부팅 시 메일 모드를 한 줄로 크게 남긴다.
     * in-cluster sink 는 SMTP 왕복까지는 진짜로 동작하지만 실제 수신함엔 도달하지 않는다.
     * 이걸 로그에 남기지 않으면 "정상 발송"으로 오독된다.
     *
     * <p>UNCONFIGURED 는 ERROR 로 올린다. 이 서비스에서 메일 미발송은 제품 자체의 부정이라
     * 기동 로그만 훑어도 눈에 걸려야 한다. 기동을 거부하지 않는 이유는 {@link #health()} 참고.
     */
    @PostConstruct
    void logMailMode() {
        if (mailSender instanceof JavaMailSenderImpl impl) {
            String description = describeMailMode(impl.getHost(), impl.getPort());
            if (mailMode() == MailMode.UNCONFIGURED) {
                log.error("=== TimeCapsule mail mode: {} ===", description);
            } else {
                log.warn("=== TimeCapsule mail mode: {} ===", description);
            }
        }
    }

    /**
     * 지금 이 배선으로 메일이 실제로 나갈 수 있는지.
     *
     * <p>{@code false} 면 발송을 시도조차 하지 않는다 — 시도해봐야 전부 실패하고,
     * 그 실패가 캡슐의 재시도 예산을 먹기 때문이다({@code CapsuleNotifier} 참고).
     */
    public boolean isConfigured() {
        return mailMode() != MailMode.UNCONFIGURED;
    }

    /**
     * JavaMailSenderImpl 이 아니면 host 를 들여다볼 수 없다. 그 경우는 누군가
     * 일부러 다른 구현을 꽂은 것이므로 설정된 것으로 본다 — 여기서 UNCONFIGURED 로
     * 단정하면 커스텀 sender 를 쓰는 배포에서 알림이 통째로 멈춘다.
     */
    public MailMode mailMode() {
        return mailSender instanceof JavaMailSenderImpl impl ? resolveMode(impl.getHost()) : MailMode.SMTP;
    }

    static MailMode resolveMode(String host) {
        if (host == null || host.isBlank() || "localhost".equals(host) || "127.0.0.1".equals(host)) {
            return MailMode.UNCONFIGURED;
        }
        if (host.contains("mailpit") || host.contains("mail-sink")) {
            return MailMode.SINK;
        }
        return MailMode.SMTP;
    }

    static String describeMailMode(String host, int port) {
        return switch (resolveMode(host)) {
            case UNCONFIGURED -> "UNCONFIGURED (host=" + host + ":" + port + ") — 메일이 발송되지 않습니다";
            case SINK -> "SINK (" + host + ":" + port + ") — SMTP 경로는 동작하지만 실제 수신함으로는 가지 않습니다";
            case SMTP -> "SMTP (" + host + ":" + port + ")";
        };
    }

    /**
     * 메일 배선 상태를 밖에서 볼 수 있게 한다. {@code /actuator/health/mail} 그룹으로
     * 노출된다(application.yml).
     *
     * <p><b>절대로 DOWN 을 돌려주면 안 된다.</b> {@code management.health.mail.enabled: false}
     * 는 의도된 설정이다 — SMTP 도달성이 readiness 를 좌우하면 메일 장애가 곧 사이트 전체
     * 다운이 된다(캡슐 작성·조회·개봉은 메일과 무관하게 정상 동작한다). DOWN 은 루트
     * {@code /actuator/health} 를 503 으로 만들어 같은 사고를 되돌린다.
     *
     * <p>그래서 UNKNOWN 을 쓴다. 상태 집계 순서상 UNKNOWN 은 UP 을 밀어내지 못해
     * 루트 health 는 200 을 유지하고, {@code /actuator/health/mail} 에서는 그대로 보인다.
     *
     * <p>host/port 는 담지 않는다 — 이 엔드포인트는 인증 없이 열려 있다(SpringSecurity).
     */
    @Override
    public Health health() {
        MailMode mode = mailMode();
        return (mode == MailMode.UNCONFIGURED ? Health.unknown() : Health.up())
                .withDetail("mode", mode.name())
                .withDetail("deliverable", mode != MailMode.UNCONFIGURED)
                .build();
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
