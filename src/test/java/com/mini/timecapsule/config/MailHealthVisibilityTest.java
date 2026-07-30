package com.mini.timecapsule.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 메일 배선 상태를 밖에서 볼 수 있게 하되, 그것이 <b>readiness 를 건드리지 않게</b> 한다.
 *
 * <p>둘 다 필요하다. 관측 수단이 없으면 MAIL_HOST 시크릿을 안 만들고 배포해도 아무도
 * 모른 채 캡슐이 쌓인다. 반대로 SMTP 도달성을 readiness 에 물리면 메일 장애 한 번에
 * 사이트 전체가 내려간다 — 캡슐 작성·조회·개봉은 메일과 아무 상관이 없는데도.
 * 그래서 {@code management.health.mail.enabled: false} 는 그대로 두고,
 * 별도 그룹({@code /actuator/health/mail})으로만 상태를 노출한다.
 *
 * <p>메일 호스트를 localhost 로 고정한다 = "MAIL_HOST 시크릿 없이 매니페스트만 적용한
 * 파드". 검증하려는 위험 상태가 바로 이것이라, 실행 환경에 MAIL_HOST 가 떠 있으면
 * 무해한 상태를 검사하며 통과해버린다.
 *
 * <p>이 테스트가 깨진다면 십중팔구 인디케이터가 DOWN 을 내기 시작한 것이다.
 * 그러면 루트 health 가 503 이 되고, 누군가 probe 경로를 루트로 바꾸는 순간
 * 메일 오설정이 곧 전면 장애가 된다.
 */
@SpringBootTest(properties = "spring.mail.host=localhost")
@AutoConfigureMockMvc
class MailHealthVisibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("메일이 설정되지 않아도 readiness 는 UP 이다")
    void readinessIsNotGatedByMailConfiguration() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("메일이 설정되지 않아도 루트 health 는 200/UP 이다")
    void rootHealthStaysUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("메일 그룹은 배선 상태를 그대로 보여준다")
    void mailGroupExposesTheActualMode() throws Exception {
        mockMvc.perform(get("/actuator/health/mail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.capsuleNotificationMailer.details.mode").value("UNCONFIGURED"))
                .andExpect(jsonPath("$.components.capsuleNotificationMailer.details.deliverable").value(false));
    }
}
