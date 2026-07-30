package com.mini.timecapsule.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 타임캡슐은 <b>비회원</b> 서비스다. 회원 관련 엔드포인트는 존재해서는 안 된다.
 *
 * <p>프론트에서 Login.vue / Join.vue 를 지웠지만 백엔드는 그대로 남아 있었고,
 * 그 결과 운영에서 이런 상태였다:
 *
 * <pre>
 * $ curl -X POST https://timecapsule.chatflow.ai.kr/api/timecapsule/login \
 *        -H 'Content-Type: application/json' -d '{"name":"attacker","coordinates":"1"}'
 * HTTP 200
 * eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
 * </pre>
 *
 * <p>비밀번호 검증이 아예 없어서 아무나 서명된 JWT 를 받아갈 수 있었다. 게다가
 * SecurityConfig 가 {@code /api/timecapsule/**} 와 {@code /h2-console/**} 를 통째로
 * permitAll 해 두었고, {@code /api/timecapsule/join} 은 인증 없이 DB 에 쓰는 엔드포인트였다.
 *
 * <p>"안 쓰는 코드니까 괜찮다"가 성립하지 않는다 — 매핑되어 있으면 공격 표면이다.
 * 다시 들어오는 것을 막기 위해 배선 자체를 못 박는다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoMemberEndpointsTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TestRestTemplate restTemplate;

    // actuator 가 controllerEndpointHandlerMapping 을 같은 타입으로 하나 더 등록한다.
    // 우리가 보고 싶은 건 @Controller 매핑 쪽이라 이름으로 못 박는다.
    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    @DisplayName("/api/timecapsule/** 로 매핑된 핸들러가 하나도 없다")
    void noMemberHandlersAreMapped() {
        List<String> memberMappings = handlerMapping.getHandlerMethods().entrySet().stream()
                .filter(entry -> mappingTouchesMemberNamespace(entry.getKey()))
                .map(entry -> entry.getKey() + " → " + describe(entry.getValue()))
                .sorted()
                .toList();

        assertEquals(List.of(), memberMappings,
                "비회원 서비스에 회원 엔드포인트가 매핑되어 있다. 매핑되어 있으면 곧 공격 표면이다");
    }

    @Test
    @DisplayName("무인증 로그인으로 토큰을 발급받을 수 없다")
    void loginEndpointIssuesNoToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/timecapsule/login", HttpMethod.POST,
                new HttpEntity<>("{\"name\":\"attacker\",\"coordinates\":\"1\"}", headers),
                String.class);

        // 핸들러가 사라졌으니 permitAll 목록에도 없다 → anyRequest().authenticated() 가
        // 먼저 걸려 403 이 난다. 404 를 못 박으면 그 규칙까지 못 바꾸게 되므로
        // "성공하지 않는다 + 토큰이 안 나온다" 만 검증한다. 매핑 부재 자체는 위 테스트가 본다.
        assertTrue(response.getStatusCode().is4xxClientError(),
                "로그인 엔드포인트가 살아 있다 (" + response.getStatusCode() + "). "
                        + "살아 있으면 아무나 서명된 JWT 를 받아간다");

        String body = response.getBody() == null ? "" : response.getBody();
        assertFalse(body.contains("eyJ"), "응답에 JWT 가 들어 있다: " + body);
    }

    @Test
    @DisplayName("JWT 발급 기계 자체가 컨텍스트에 없다")
    void noTokenIssuingBeansExist() {
        Map<String, Object> jwtBeans = context.getBeansOfType(Object.class);

        List<String> offenders = jwtBeans.entrySet().stream()
                .filter(entry -> entry.getValue() != null
                        && entry.getValue().getClass().getName().startsWith("com.mini.timecapsule.jwt."))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        assertEquals(List.of(), offenders,
                "토큰 발급기가 빈으로 남아 있다. 쓰는 곳이 없어도 시크릿 설정이 계속 딸려 온다");
    }

    @Test
    @DisplayName("H2 콘솔은 permitAll 대상이 아니다")
    void h2ConsoleIsNotPubliclyPermitted() {
        ResponseEntity<String> response = restTemplate.getForEntity("/h2-console/login.do", String.class);

        assertTrue(response.getStatusCode().is4xxClientError(),
                "H2 콘솔 경로가 열려 있다 (" + response.getStatusCode() + "). "
                        + "콘솔 서블릿이 지금 꺼져 있더라도 permitAll 규칙을 남겨두면 "
                        + "누가 spring.h2.console.enabled 를 켜는 순간 DB 가 통째로 공개된다");
    }

    private boolean mappingTouchesMemberNamespace(RequestMappingInfo mapping) {
        return mapping.toString().contains("/api/timecapsule");
    }

    private String describe(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().getSimpleName() + "#" + handlerMethod.getMethod().getName();
    }
}
