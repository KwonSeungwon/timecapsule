package com.mini.timecapsule.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 프레임워크가 만들어내는 4xx 도 <b>사용자가 읽는 문구</b>다.
 *
 * <p>WriteLetter.vue / myCapsule.vue 는 {@code error.response.data.message} 를 그대로
 * 팝업에 띄운다. 즉 서버가 넣은 문자열이 곧 UI 텍스트다. 그런데 아래 두 부류가
 * 그 원칙을 새고 있었다.
 *
 * <p><b>(1) 타입이 안 맞는 path variable 이 500 이었다.</b>
 * {@code POST /api/v1/capsule/not-a-number/open} 은
 * {@code MethodArgumentTypeMismatchException} 을 던지는데, 이 예외는 Spring 의
 * {@code ErrorResponse} 인터페이스를 구현하지 <b>않는다</b>. 그래서
 * {@code GlobalExceptionHandler} 의 {@code instanceof ErrorResponse} 분기를 그냥 지나쳐
 * 일반 핸들러로 떨어졌고, 결과는 <b>500 + {@code log.error} 스택트레이스</b>였다.
 * 봇이 긁거나, 오래된 링크가 남아 있거나, 프론트 버그로 {@code capsule/undefined/open}
 * 이 한 번 나가면 그때마다 ERROR 로그가 쌓인다. 클라이언트 잘못은 400 이고
 * ERROR 로 남기면 안 된다.
 *
 * <p><b>(2) 프레임워크 영문 메시지가 그대로 사용자에게 나갔다.</b> 실측:
 * <pre>
 * GET    /api/v1/capsule/list     → {"message":"Required parameter 'email' is not present."}
 * DELETE /api/v1/capsule/public   → {"message":"Method 'DELETE' is not supported."}
 * </pre>
 * {@code ErrorResponse} 분기가 프레임워크의 {@code detail} 을 그대로 실어 보냈기 때문이다.
 * 영어인 것도 문제지만, {@code detail} 은 파라미터명·경로·Content-Type 같은 내부 계약을
 * 그대로 노출한다. {@code detail} 은 <b>로그에만</b> 남기고 사용자에게는 상태코드별로
 * 고정된 한국어 문구를 준다.
 *
 * <p><b>MockMvc 로 되돌리지 말 것.</b> 이 경로들은 실제 서블릿 컨테이너의 필터 체인과
 * {@code DispatcherServlet} 예외 처리를 모두 통과해야 재현된다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FrameworkErrorMessageTest {

    @Autowired
    private TestRestTemplate rest;

    // --- T1: path variable 타입 불일치는 400 이다 (500 이 아니라) ---

    @Test
    @DisplayName("숫자가 아닌 캡슐 id 는 400 이다 — 500 + 스택트레이스가 아니라")
    void nonNumericPathVariableIsBadRequestNotServerError() {
        ResponseEntity<String> response = postJson(
                "/api/v1/capsule/not-a-number/open", "{\"password\":\"pw\"}");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "path variable 변환 실패는 클라이언트 잘못이다. 500 이면 봇 트래픽마다 "
                        + "ERROR 로그가 쌓이고 알람이 무의미해진다: " + response.getBody());
        assertReadableKoreanMessage(response);
        assertFalse(response.getBody().contains("not-a-number"),
                "클라이언트가 보낸 값을 그대로 되돌려주지 않는다(고정 문구여야 한다): " + response.getBody());
    }

    @Test
    @DisplayName("프론트 버그로 흔히 나가는 capsule/undefined/open 도 400 이다")
    void undefinedIdFromAFrontendBugIsAlsoBadRequest() {
        // JS 에서 id 가 undefined 면 템플릿 리터럴이 문자열 "undefined" 를 만든다.
        // 이 실수는 반드시 한 번은 일어나고, 그때 서버가 500 을 내면 안 된다.
        ResponseEntity<String> response = postJson(
                "/api/v1/capsule/undefined/open", "{\"password\":\"pw\"}");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), response.getBody());
        assertReadableKoreanMessage(response);
    }

    // --- T2: 프레임워크 4xx 는 한국어 고정 문구로 바꿔 내보낸다 ---

    @Test
    @DisplayName("필수 쿼리 파라미터 누락은 400 + 한국어다 — \"Required parameter 'email' is not present.\" 가 아니라")
    void missingRequiredParamIsKorean() {
        ResponseEntity<String> response = rest.getForEntity("/api/v1/capsule/list", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), response.getBody());
        assertReadableKoreanMessage(response);
        assertFalse(response.getBody().contains("Required parameter"),
                "프레임워크 영문 detail 이 그대로 나갔다: " + response.getBody());
        assertFalse(response.getBody().contains("email"),
                "내부 파라미터명이 사용자 문구에 실려 나갔다: " + response.getBody());
    }

    @Test
    @DisplayName("허용되지 않은 HTTP 메서드는 405 + 한국어다 — \"Method 'DELETE' is not supported.\" 가 아니라")
    void unsupportedMethodIsKorean() {
        ResponseEntity<String> response = rest.exchange(
                "/api/v1/capsule/public", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode(), response.getBody());
        assertReadableKoreanMessage(response);
        assertFalse(response.getBody().contains("is not supported"),
                "프레임워크 영문 detail 이 그대로 나갔다: " + response.getBody());
    }

    @Test
    @DisplayName("지원하지 않는 Content-Type 은 415 + 한국어다")
    void unsupportedMediaTypeIsKorean() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        ResponseEntity<String> response = rest.exchange("/api/v1/capsule", HttpMethod.POST,
                new HttpEntity<>("아무 텍스트", headers), String.class);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode(), response.getBody());
        assertReadableKoreanMessage(response);
        assertFalse(response.getBody().contains("not supported"),
                "프레임워크 영문 detail 이 그대로 나갔다: " + response.getBody());
    }

    @Test
    @DisplayName("없는 API 경로는 404 + 한국어다 — 내부 리소스 경로를 노출하지 않는다")
    void unknownApiPathIsKorean() {
        ResponseEntity<String> response =
                rest.getForEntity("/api/v1/capsule/no-such-endpoint-here", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), response.getBody());
        assertReadableKoreanMessage(response);
        assertFalse(response.getBody().contains("No static resource"),
                "프레임워크가 만든 내부 문구가 그대로 나갔다: " + response.getBody());
    }

    // --- 공통 계약: 사용자가 읽을 수 있는 한국어여야 하고 내부 정보가 없어야 한다 ---

    /**
     * 모든 에러 응답이 지켜야 하는 것: {@code message} 가 비어 있지 않고, 한글이 들어 있고,
     * 예외 클래스명·패키지명·SQL 같은 내부 정보가 없다.
     *
     * <p>이 단언이 깨지면 사용자는 팝업에서 영문 스택 조각이나 {@code null} 을 보게 된다.
     */
    private void assertReadableKoreanMessage(ResponseEntity<String> response) {
        String body = response.getBody();
        assertNotNull(body, "본문이 비면 프론트가 사유를 표시할 수 없다");

        assertTrue(body.contains("\"message\""), "message 필드가 있어야 한다: " + body);
        assertTrue(body.codePoints().anyMatch(cp -> cp >= 0xAC00 && cp <= 0xD7A3),
                "사용자에게 보여줄 한국어 문구가 없다: " + body);

        for (String leak : new String[]{"Exception", "org.springframework", "java.lang",
                "at com.mini", "select ", "SQLException"}) {
            assertFalse(body.contains(leak),
                    "내부 정보(" + leak + ")가 응답에 실려 나갔다: " + body);
        }
    }

    private ResponseEntity<String> postJson(String path, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return rest.exchange(path, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }
}
