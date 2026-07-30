package com.mini.timecapsule.controller;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.model.ContentFilter;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.service.LetterService;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 실패 응답은 프론트가 사용자에게 보여줄 수 있어야 한다.
 *
 * <p>WriteLetter.vue / myCapsule.vue 는 {@code error.response.data.message} 를 읽어
 * 팝업에 띄운다. 본문 없이 상태코드만 돌려주면 사용자는 "알 수 없는 오류" 만 본다.
 *
 * <p>실제로 그런 상태였다. {@code customizedExceptionHandler} 라는 두 번째
 * {@code @ControllerAdvice} 가 모든 {@code CustomException} 을 가로채서 HTTP 상태코드
 * 자리에 {@code 404002}(회원 시절 사내 에러코드)를 넣었고, Spring 이
 * {@code IllegalArgumentException: Status code '404002' should be a three-digit
 * positive integer} 로 그 핸들러를 포기하면서 원래 예외가 그대로 필터 체인까지
 * 올라가 <b>403 + 빈 본문</b>이 나갔다.
 *
 * <p><b>MockMvc 로는 재현되지 않는다</b> — 두 advice 중 어느 쪽이 뽑히는지가
 * 갈리고, 무엇보다 예외가 필터 체인까지 올라가는 경로가 없다. 그래서 실제
 * 서블릿 컨테이너({@code RANDOM_PORT})를 띄운다. 이 테스트를 MockMvc 로
 * 되돌리지 말 것.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LetterControllerErrorTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private LetterService letterService;

    @Autowired
    private ContentFilterRepository contentFilterRepository;

    private Long capsuleId;

    @BeforeEach
    void setUp() {
        LetterPaper saved = letterService.sendLetter(CapsuleCreateRequest.builder()
                .senderName("보낸이")
                .title("오류 테스트 캡슐")
                .receiverEmail("err@example.com")
                .password("correct-pw")
                .content("내용입니다")
                .openAt(ZonedDateTime.now().plusDays(1))
                .letterPaperType(LetterPaper.LetterPaperType.LETTER)
                .capsuleType(LetterPaper.CapsuleType.BOTTLE)
                .isPublic(false)
                .build());
        capsuleId = saved.getId();
    }

    private ResponseEntity<String> postJson(String path, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return rest.exchange(path, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }

    @Test
    @DisplayName("비밀번호가 틀리면 400 과 읽을 수 있는 message 를 준다 (403 빈 본문이 아니라)")
    void wrongPasswordReturnsBadRequestWithMessage() {
        ResponseEntity<String> response =
                postJson("/api/v1/capsule/" + capsuleId + "/open", "{\"password\":\"wrong-pw\"}");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "본문이 비면 프론트가 사유를 표시할 수 없다");
        assertTrue(response.getBody().contains("비밀번호가 일치하지 않습니다"),
                "비밀번호 오류가 한국어로 담겨야 한다: " + response.getBody());
    }

    @Test
    @DisplayName("비밀번호가 비어 있으면 400 과 한국어 안내를 준다")
    void blankPasswordIsRejectedBeforeItReachesTheEncoder() {
        ResponseEntity<String> response =
                postJson("/api/v1/capsule/" + capsuleId + "/open", "{\"password\":\"\"}");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().contains("비밀번호는 필수입니다."),
                "안내 문구가 담겨야 한다: " + response.getBody());
    }

    @Test
    @DisplayName("비속어가 걸리면 400 과 그 사유를 준다 — 'USER NOT FOUND' 가 아니라")
    void forbiddenKeywordReturnsItsOwnReason() {
        contentFilterRepository.save(ContentFilter.newContentFilter("바보", ContentFilter.Used.ACTIVE));

        ResponseEntity<String> response = postJson("/api/v1/capsule",
                capsuleJson("a@example.com", ZonedDateTime.now().plusDays(30))
                        .replace("\"내용입니다\"", "\"너 바보야\""));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() != null
                        && response.getBody().contains("비속어"),
                "비속어 사유가 한국어로 담겨야 한다: " + response.getBody());
    }

    // --- F2: 깨진 JSON 이 500 을 내면 안 된다 ---

    @Test
    @DisplayName("깨진 JSON 은 400 이다 — 500 이 아니라, 그리고 파싱 에러의 내부 클래스명을 노출하지 않는다")
    void malformedJsonReturnsBadRequestNotInternalServerError() {
        // 실제로 JSON 이 아닌 문자열을 보내면 Jackson 이
        // HttpMessageNotReadableException 을 던진다. 이걸 안 잡으면
        // handleGeneralException 이 잡고 500 + e.getMessage() 를 뱉어서
        // 내부 구현이 그대로 밖으로 나간다.
        ResponseEntity<String> response = postJson("/api/v1/capsule", "이건 JSON이 아니다{{{");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "JSON 파싱 실패는 클라이언트 잘못이므로 400 이어야 한다");
        assertFalse(response.getBody().contains("HttpMessageNotReadableException"),
                "내부 예외 클래스명이 응답에 실려 나가면 안 된다: " + response.getBody());
        assertFalse(response.getBody().contains("JsonParseException"),
                "Jackson 내부 예외 클래스명이 응답에 실려 나가면 안 된다: " + response.getBody());
    }

    // --- F3: 이메일/개봉일 검증 ---

    /** 개봉일을 상대 시각으로 만든다 — 리터럴 연도를 박으면 그 해가 오는 순간 테스트가 거짓말을 시작한다. */
    private static String capsuleJson(String email, ZonedDateTime openAt) {
        return """
                {"senderName":"보낸이","title":"제목","receiverEmail":"%s",
                 "password":"pw","content":"내용입니다","openAt":"%s",
                 "isPublic":false,"letterPaperType":"LETTER","capsuleType":"BOTTLE"}
                """.formatted(email, openAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Test
    @DisplayName("이메일 형식이 아닌 receiverEmail 은 400 으로 거부한다")
    void invalidEmailFormatIsRejected() {
        ResponseEntity<String> response = postJson("/api/v1/capsule",
                capsuleJson("asdf", ZonedDateTime.now().plusDays(30)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().contains("이메일"),
                "이메일 형식 오류 안내가 담겨야 한다: " + response.getBody());
    }

    @Test
    @DisplayName("과거 날짜의 openAt 은 400 으로 거부한다 — 타임캡슐이니까 미래여야 한다")
    void pastOpenAtIsRejected() {
        ResponseEntity<String> response = postJson("/api/v1/capsule",
                capsuleJson("a@example.com", ZonedDateTime.now().minusDays(1)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().contains("개봉일"),
                "과거 날짜 거부 안내가 담겨야 한다: " + response.getBody());
    }

    @Test
    @DisplayName("정상 요청은 여전히 200 으로 캡슐이 만들어진다")
    void validRequestStillSucceeds() {
        ResponseEntity<String> response = postJson("/api/v1/capsule",
                capsuleJson("valid@example.com", ZonedDateTime.now().plusDays(30)));

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "정상 요청이 거부되면 안 된다: " + response.getBody());
    }
}
