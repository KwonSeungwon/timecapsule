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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 운영에서 캡슐이 사라지지 않게 하는 설정을 못 박는다.
 *
 * <p>타임캡슐은 "몇 달 뒤에 열어본다"가 제품의 전부다. 파드가 재시작될 때마다
 * 스키마가 새로 만들어지면 서비스 자체가 성립하지 않는다.
 *
 * <p>실제로 그런 상태였다. {@code application.yml} 은 {@code ddl-auto: update} 였지만
 * 같은 디렉터리의 {@code application.yml} 보다 <b>나중에</b> 로드되는
 * {@code application.properties} 에 {@code ddl-auto=create} 가 남아 있어서 그쪽이 이겼다.
 * PVC 도 붙어 있고 {@code .mv.db} 파일도 그대로였는데 기동할 때마다 테이블을
 * drop/create 하니 캡슐만 증발했다 — 로그에는 아무 에러도 없다.
 *
 * <p>이 테스트는 앱을 띄우지 않고 설정 파일만 로드해서 <b>최종 병합된</b> 값을 본다.
 * 파일 하나만 읽는 검사로는 오버라이드 관계를 못 잡기 때문이다.
 */
class PersistenceConfigTest {

    @Configuration
    static class Empty {
    }

    /** 스키마를 지우는 값들. 운영에서 이 중 하나라도 걸리면 캡슐이 날아간다. */
    private static void assertNeverWipesSchema(Environment environment, String profile) {
        String ddlAuto = environment.getProperty("spring.jpa.hibernate.ddl-auto");
        assertEquals("update", ddlAuto,
                "[" + profile + "] ddl-auto 가 'update' 가 아니면 기동할 때마다 캡슐이 사라진다. "
                        + "application.yml 뿐 아니라 application.properties 도 확인할 것 "
                        + "(.properties 가 .yml 을 덮어쓴다)");
    }

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = Empty.class, initializers = ConfigDataApplicationContextInitializer.class)
    @ActiveProfiles("prod")
    @DisplayName("prod 프로필")
    static class Prod {

        @Autowired
        private Environment environment;

        @Test
        @DisplayName("기동할 때 스키마를 다시 만들지 않는다")
        void doesNotRecreateSchemaOnBoot() {
            assertNeverWipesSchema(environment, "prod");
        }

        @Test
        @DisplayName("서블릿 포트는 3000 이다 (Dockerfile EXPOSE / k8s probe 와 같은 값)")
        void servesOnPortThreeThousand() {
            assertEquals("3000", environment.getProperty("server.port"),
                    "이미지와 readiness/liveness probe 가 3000 을 본다");
        }
    }

    @ExtendWith(SpringExtension.class)
    @ContextConfiguration(classes = Empty.class, initializers = ConfigDataApplicationContextInitializer.class)
    @DisplayName("기본 프로필")
    static class Default {

        @Autowired
        private Environment environment;

        @Test
        @DisplayName("기동할 때 스키마를 다시 만들지 않는다")
        void doesNotRecreateSchemaOnBoot() {
            assertNeverWipesSchema(environment, "default");
        }
    }
}
