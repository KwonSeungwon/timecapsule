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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertTrue(response.getBody().contains("INVALID PASSWORD"),
                "실제 사유가 담겨야 한다: " + response.getBody());
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

        ResponseEntity<String> response = postJson("/api/v1/capsule", """
                {"senderName":"보낸이","title":"제목","receiverEmail":"a@example.com",
                 "password":"pw","content":"너 바보야","openAt":"2030-01-01T00:00:00+09:00",
                 "isPublic":false,"letterPaperType":"LETTER","capsuleType":"BOTTLE"}
                """);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() != null
                        && response.getBody().contains("CONTENT CONTAINS FORBIDDEN KEYWORDS"),
                "비속어 사유가 담겨야 한다: " + response.getBody());
    }
}
