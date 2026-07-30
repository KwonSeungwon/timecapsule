package com.mini.timecapsule.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * vue-router 가 createWebHistory(history mode) 라 /write · /my-capsule 같은 딥링크가
 * 서버를 반드시 거친다. prod 에서 이 경로들이 전부 403 이라 랜딩(/) 외 모든 화면이
 * 열리지 않았다. 그 회귀를 잡는 테스트.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SpaRoutingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void spaDeepLinksServeIndexHtml() throws Exception {
        for (String path : new String[]{"/write", "/find", "/my-capsule", "/tutorial", "/explore"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/html"));
        }
    }

    @Test
    void deepLinkWithQueryStringIsServed() throws Exception {
        mockMvc.perform(get("/my-capsule").param("email", "someone@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    void rootIsServed() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void apiRoutesAreNotSwallowedBySpaFallback() throws Exception {
        mockMvc.perform(get("/api/v1/capsule/public"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void missingStaticAssetReturns404NotIndexHtml() throws Exception {
        mockMvc.perform(get("/js/does-not-exist.js")).andExpect(status().isNotFound());
    }

    @Test
    void actuatorHealthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
