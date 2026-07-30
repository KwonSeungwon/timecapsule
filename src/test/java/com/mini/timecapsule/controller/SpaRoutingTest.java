package com.mini.timecapsule.controller;

import com.mini.timecapsule.config.SpaForwardingConfig;
import com.mini.timecapsule.configurations.SpringSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * vue-router 가 createWebHistory(history mode) 라 /write · /my-capsule 같은 딥링크가
 * 서버를 반드시 거친다. prod 에서 이 경로들이 전부 403 이라 랜딩(/) 외 모든 화면이
 * 열리지 않았다. 그 회귀를 잡는 테스트.
 *
 * <p><b>그 뒤에 남아 있던 더 큰 문제</b>: SecurityConfig 가 SPA 라우트를 <i>손으로</i>
 * 하나씩 whitelist 하고 나머지는 {@code anyRequest().authenticated()} 로 막고 있었다.
 * 즉 <b>기본값이 "차단"</b>이었다. 결과:
 * <ul>
 *   <li>vue-router 의 {@code catchAll}(404 화면)이 하드 로드/새로고침에서 죽는다 —
 *       Security 가 먼저 403 을 내서 SPA 가 아예 로드되지 않는다.</li>
 *   <li>오타 URL 이 앱 대신 <b>빈 403</b> 을 준다.</li>
 *   <li>앞으로 추가되는 모든 Vue 라우트가, 누군가 이 Java 파일을 같이 고치는 걸
 *       기억하지 못하면, 운영에서 조용히 403 이 된다.</li>
 * </ul>
 * 그래서 규칙을 뒤집었다: "SPA 가 아닌 것"만 명시하고 나머지는 열어 둔다.
 * 아래 {@link #arbitraryFutureRouteServesTheSpaShell()} 이 그 뒤집기를 못 박는다.
 *
 * <p><b>MockMvc 가 아니라 실제 컨테이너(RANDOM_PORT)를 띄운다.</b> 여기서 잡으려는 것은
 * Security 필터 체인과 리소스 핸들러가 <i>실제로 겹치는 순서</i>이고, 빈 403 은
 * 필터 단계에서 만들어진다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpaRoutingTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    @DisplayName("SPA 딥링크는 index.html 을 준다")
    void spaDeepLinksServeIndexHtml() {
        for (String path : new String[]{"/write", "/find", "/my-capsule", "/tutorial", "/explore"}) {
            assertServesSpaShell(rest.getForEntity(path, String.class), path);
        }
    }

    @Test
    @DisplayName("쿼리스트링이 붙은 딥링크도 index.html 을 준다")
    void deepLinkWithQueryStringIsServed() {
        assertServesSpaShell(
                rest.getForEntity("/my-capsule?email=someone@example.com", String.class),
                "/my-capsule?email=...");
    }

    @Test
    @DisplayName("루트는 열려 있다")
    void rootIsServed() {
        assertEquals(HttpStatus.OK, rest.getForEntity("/", String.class).getStatusCode());
    }

    /**
     * <b>이 테스트가 Task 4 의 본체다.</b>
     *
     * <p>whitelist 방식에서는 여기에 적힌 경로가 <b>403 + 빈 본문</b>이었다. 즉
     * 내년에 누가 Vue 에 새 라우트를 추가하면, 이 Java 파일을 같이 고치는 걸 기억하지
     * 못하는 한, 개발 환경(해시 라우팅 없이 SPA 내부 이동)에서는 멀쩡하다가
     * <b>운영에서 새로고침할 때만</b> 깨진다. 가장 늦게 발견되는 종류의 버그다.
     *
     * <p>이 테스트가 깨졌다면 SecurityConfig 가 다시 whitelist 방식으로 돌아간 것이다.
     * 경로를 하나 추가해서 통과시키지 말고, 기본값이 permitAll 인지부터 확인할 것.
     */
    @Test
    @DisplayName("등록된 적 없는 임의의 경로도 SPA 셸을 준다 — 빈 403 이 아니라")
    void arbitraryFutureRouteServesTheSpaShell() {
        ResponseEntity<String> response =
                rest.getForEntity("/totally-new-route-added-next-year", String.class);

        assertServesSpaShell(response, "/totally-new-route-added-next-year");
    }

    @Test
    @DisplayName("여러 단계로 중첩된 미래 라우트도 SPA 셸을 준다")
    void nestedFutureRouteServesTheSpaShell() {
        assertServesSpaShell(
                rest.getForEntity("/capsule/2031/share/abc", String.class),
                "/capsule/2031/share/abc");
    }

    @Test
    @DisplayName("API 는 SPA fallback 에 삼켜지지 않는다")
    void apiRoutesAreNotSwallowedBySpaFallback() {
        ResponseEntity<String> response = rest.getForEntity("/api/v1/capsule/public", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON),
                "API 가 HTML 을 돌려주면 axios 가 JSON 파싱에서 죽는다: "
                        + response.getHeaders().getContentType());
    }

    @Test
    @DisplayName("없는 정적 자원은 404 다 — index.html 이 아니라")
    void missingStaticAssetReturns404NotIndexHtml() {
        // 여기서 index.html 을 돌려주면 브라우저가 HTML 을 JS 로 파싱하려다
        // "Unexpected token '<'" 로 죽는다. 원인 추적이 매우 어려운 부류다.
        assertEquals(HttpStatus.NOT_FOUND,
                rest.getForEntity("/js/does-not-exist.js", String.class).getStatusCode());
    }

    @Test
    @DisplayName("actuator health 는 공개다 (k8s probe)")
    void actuatorHealthIsPublic() {
        assertEquals(HttpStatus.OK, rest.getForEntity("/actuator/health", String.class).getStatusCode());
    }

    @Test
    @DisplayName("공개하지 않은 actuator 엔드포인트는 열리지 않는다")
    void nonProbeActuatorEndpointsStayClosed() {
        // anyRequest() 를 permitAll 로 뒤집었으니, actuator 가 통째로 열리지 않는지
        // 명시적으로 확인한다. /actuator/env 는 환경변수(=시크릿)를 그대로 뱉는다.
        assertTrue(rest.getForEntity("/actuator/env", String.class).getStatusCode().is4xxClientError(),
                "actuator 가 통째로 열렸다 — /actuator/env 는 환경변수를 그대로 노출한다");
    }

    /**
     * <b>두 목록이 갈라지는 것을 막는 테스트.</b>
     *
     * <p>"무엇이 SPA 라우트가 아닌가" 는 {@link SpaForwardingConfig#NON_SPA_PREFIXES} 에만
     * 적혀 있어야 한다. SecurityConfig 는 그 목록에서 <i>공개 정적 자원</i>
     * ({@link SpringSecurity#PUBLIC_ASSET_PREFIXES}) 만 빼고 나머지를 자동으로 잠근다.
     *
     * <p>이 테스트는 공개 자산 목록이 원본 목록의 부분집합인지만 본다. 오타로
     * {@code "imgs/"} 같은 걸 적어두면 그 항목은 아무것도 열지 못하면서 이름만 남는다.
     * (반대 방향 — 새 prefix 를 추가하는 경우 — 은 테스트가 아니라 <b>설계</b>로 막는다:
     * 분류되지 않은 prefix 는 자동으로 "잠금" 쪽에 떨어진다. fail-open 이 아니라 fail-safe.)
     */
    @Test
    @DisplayName("공개 정적 자원 목록은 NON_SPA_PREFIXES 의 부분집합이다")
    void publicAssetPrefixesStayInSyncWithSpaForwardingConfig() {
        List<String> nonSpa = Arrays.asList(SpaForwardingConfig.NON_SPA_PREFIXES);

        List<String> unknown = SpringSecurity.PUBLIC_ASSET_PREFIXES.stream()
                .filter(prefix -> !nonSpa.contains(prefix))
                .sorted()
                .toList();

        assertEquals(List.of(), unknown,
                "SpringSecurity 가 SpaForwardingConfig.NON_SPA_PREFIXES 에 없는 prefix 를 "
                        + "공개 자산으로 분류하고 있다. 두 목록이 갈라졌다. 실제 목록="
                        + new TreeSet<>(nonSpa));
    }

    private void assertServesSpaShell(ResponseEntity<String> response, String path) {
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                path + " 가 SPA 셸을 주지 않는다. 403 이면 SecurityConfig 가 다시 "
                        + "whitelist 방식으로 돌아간 것이다");
        assertNotNull(response.getHeaders().getContentType(), path + " 응답에 Content-Type 이 없다");
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.TEXT_HTML),
                path + " 가 HTML 이 아니다: " + response.getHeaders().getContentType());
    }
}
