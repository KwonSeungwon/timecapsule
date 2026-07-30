package com.mini.timecapsule.service;

import com.mini.timecapsule.model.LetterPaper;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 Spring 이 조립한 SpringTemplateEngine 으로 검증한다.
 * 순수 org.thymeleaf.TemplateEngine 을 직접 만들면 OGNL 을 찾는데
 * spring-boot-starter-thymeleaf 는 SpringEL 만 가져오므로 런타임에 터진다.
 * 프로덕션 배선(@Value 로 주입되는 base-url/from 포함)을 그대로 태우는 편이 낫다.
 */
@SpringBootTest(properties = {
        "timecapsule.notification.base-url=https://timecapsule.chatflow.ai.kr",
        "timecapsule.notification.from=noreply@example.com"
})
class CapsuleNotificationMailerTest {

    @Autowired
    private CapsuleNotificationMailer mailer;

    @Autowired
    private ITemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    private LetterPaper capsule() {
        return LetterPaper.newEntity(
                "보낸이", "10년 뒤의 나에게", "receiver@example.com", "enc",
                120, 880, "DESERT", "붉은 사막",
                LetterPaper.LetterPaperType.LETTER, LetterPaper.CapsuleType.BOTTLE,
                "내용", null, null, ZonedDateTime.now().minusMinutes(1), false);
    }

    @Test
    void buildsHtmlMessageWithCapsuleDetailsAndHttpsLink() throws Exception {
        MimeMessage message = mailer.compose(capsule());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
        String raw = out.toString("UTF-8");
        String body = mailer.renderBody(capsule());

        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("receiver@example.com");
        assertThat(raw).contains("text/html");
        assertThat(body).contains("붉은 사막");
        assertThat(body).contains("보낸이");
        assertThat(body).contains("10년 뒤의 나에게");
        assertThat(body).contains("120");
        assertThat(body).contains("880");
        assertThat(body).contains("https://timecapsule.chatflow.ai.kr/my-capsule?email=receiver%40example.com");
        assertThat(body).doesNotContain("http://timecapsule");
    }

    @Test
    void subjectCarriesTheCapsuleTitle() throws Exception {
        assertThat(mailer.compose(capsule()).getSubject()).contains("10년 뒤의 나에게");
    }

    @Test
    void escapesEmailInLinkQueryString() {
        LetterPaper c = capsule();
        c.setReceiverEmail("a+b@example.com");
        assertThat(mailer.renderBody(c)).contains("a%2Bb%40example.com");
    }

    @Test
    void trailingSlashInBaseUrlDoesNotProduceDoubleSlash() {
        CapsuleNotificationMailer withSlash = new CapsuleNotificationMailer(
                mailSender, templateEngine, "https://x.test/", "noreply@example.com");
        String body = withSlash.renderBody(capsule());
        assertThat(body).contains("https://x.test/my-capsule?email=");
        assertThat(body).doesNotContain("x.test//my-capsule");
    }

    @Test
    void unknownBiomeFallsBackToGenericPin() {
        LetterPaper c = capsule();
        c.setBiome("VOLCANO");
        assertThat(mailer.renderBody(c)).contains("📍");
    }

    @Test
    void sinkModeIsReportedWhenHostIsTheInClusterSink() {
        assertThat(CapsuleNotificationMailer.describeMailMode("timecapsule-mailpit", 1025))
                .contains("SINK");
        assertThat(CapsuleNotificationMailer.describeMailMode("smtp.gmail.com", 587))
                .contains("SMTP");
        assertThat(CapsuleNotificationMailer.describeMailMode("localhost", 25))
                .contains("UNCONFIGURED");
    }
}
