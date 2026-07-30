package com.mini.timecapsule.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SMTP 타임아웃을 설정 파일에 못 박는다.
 *
 * <p>JavaMail 의 기본 타임아웃은 <b>무한</b>이다. 클라우드 egress 방화벽이 25/587 포트를
 * REJECT 가 아니라 DROP 하면 TCP 핸드셰이크가 응답 없이 매달리고,
 * {@code CapsuleNotificationMailer.send()} 가 영원히 블로킹된다.
 *
 * <p>그 결과가 이 서비스에서는 치명적이다. 스케줄러 스레드가 하나뿐이라
 * ({@code spring.task.scheduling.pool.size} 를 올리지 않았다) 한 건이 매달리는 순간
 * 알림 파이프라인 전체가 멈춘다. 예외가 발생하지 않으니 로그도 조용하고,
 * {@code management.health.mail.enabled: false} 라 파드는 계속 Ready 다.
 * "메일이 한 통도 안 나가는데 아무도 모르는" 상태가 무기한 지속된다.
 *
 * <p>이 값들을 지우면 그 상태로 되돌아간다. 값 자체보다 "유한한 값이 있다"가 핵심이라
 * 상한만 검사한다.
 */
class MailTimeoutConfigTest {

    /** 매분 스케줄(k8s TIMECAPSULE_NOTIFICATION_CRON) 안에서 여러 건을 처리해야 하므로 넉넉해도 30초를 넘기면 안 된다. */
    private static final int UPPER_BOUND_MS = 30_000;

    @Configuration
    static class Empty {
    }

    private static void assertFiniteTimeout(Environment environment, String profile, String key) {
        String value = environment.getProperty(key);
        assertNotNull(value,
                "[" + profile + "] " + key + " 가 없으면 JavaMail 기본값(무한)이 적용된다. "
                        + "SMTP 가 블랙홀이면 스케줄러 스레드가 영구히 매달려 알림이 전부 멈춘다");
        int millis = Integer.parseInt(value);
        assertTrue(millis > 0 && millis <= UPPER_BOUND_MS,
                "[" + profile + "] " + key + " 는 0 초과 " + UPPER_BOUND_MS + "ms 이하여야 한다. 현재=" + millis);
    }

    private static void assertAllSmtpTimeoutsAreFinite(Environment environment, String profile) {
        // 세 개를 다 걸어야 한다. connectiontimeout 만 걸면 연결은 되고 응답이 없는
        // 서버에서 read 가 무한 대기하고, timeout 만 걸면 연결 단계에서 매달린다.
        assertFiniteTimeout(environment, profile, "spring.mail.properties.mail.smtp.connectiontimeout");
        assertFiniteTimeout(environment, profile, "spring.mail.properties.mail.smtp.timeout");
        assertFiniteTimeout(environment, profile, "spring.mail.properties.mail.smtp.writetimeout");
    }

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = Empty.class, initializers = ConfigDataApplicationContextInitializer.class)
    @ActiveProfiles("prod")
    @DisplayName("prod 프로필")
    static class Prod {

        @Autowired
        private Environment environment;

        @Test
        @DisplayName("SMTP 연결/읽기/쓰기 타임아웃이 모두 유한하다")
        void smtpTimeoutsAreFinite() {
            assertAllSmtpTimeoutsAreFinite(environment, "prod");
        }
    }

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = Empty.class, initializers = ConfigDataApplicationContextInitializer.class)
    @DisplayName("기본 프로필")
    static class Default {

        @Autowired
        private Environment environment;

        @Test
        @DisplayName("SMTP 연결/읽기/쓰기 타임아웃이 모두 유한하다")
        void smtpTimeoutsAreFinite() {
            assertAllSmtpTimeoutsAreFinite(environment, "default");
        }
    }
}
