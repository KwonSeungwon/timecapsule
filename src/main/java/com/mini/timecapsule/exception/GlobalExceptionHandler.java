package com.mini.timecapsule.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * 이 서비스에서 <b>응답 본문의 message 는 곧 UI 텍스트</b>다.
 * WriteLetter.vue / myCapsule.vue 가 {@code error.response.data.message} 를 그대로
 * 팝업에 띄우기 때문이다.
 *
 * <p>따라서 여기서 만드는 모든 문자열은 아래를 지켜야 한다:
 * <ul>
 *   <li>사람이 읽는 한국어일 것</li>
 *   <li>예외 클래스명 · 스택프레임 · SQL · 요청 본문을 절대 포함하지 않을 것</li>
 *   <li>프레임워크가 만든 영문 {@code detail} 을 그대로 흘려보내지 않을 것
 *       — 그건 <b>로그용</b>이다</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERIC_SERVER_MESSAGE =
            "서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.";
    private static final String INVALID_REQUEST_MESSAGE = "요청값이 올바르지 않습니다.";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        // CustomException 의 메시지는 ExceptionStructure 에 정의된 한국어 상수뿐이다.
        // 여기에 e.getMessage() 를 쓰는 것은 의도적이며 안전하다.
        return body(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        // @NotBlank(message="...") 등 우리가 직접 쓴 한국어 문구만 나간다.
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(INVALID_REQUEST_MESSAGE);
        return body(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * path variable / 쿼리 파라미터의 타입 변환 실패.
     *
     * <p>{@code POST /api/v1/capsule/not-a-number/open} 같은 요청이다.
     * {@code MethodArgumentTypeMismatchException} 은 Spring 의 {@code ErrorResponse}
     * 인터페이스를 <b>구현하지 않기 때문에</b> 아래 {@link #handleGeneralException} 의
     * {@code instanceof ErrorResponse} 분기를 그냥 지나쳐 <b>500 + ERROR 스택트레이스</b>로
     * 처리되고 있었다. 봇 스캔이나 프론트의 {@code capsule/undefined/open} 버그 한 번에
     * ERROR 로그가 쌓여 알람이 무의미해진다.
     *
     * <p>이건 명백한 클라이언트 잘못이므로 400 이고, ERROR 로 남기지 않는다.
     * 변환에 실패한 값은 클라이언트가 보낸 것이라 <b>응답에 되돌려주지 않는다</b>
     * (반사 XSS 및 내부 계약 노출 방지). 진단이 필요하면 로그를 본다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.debug("요청 파라미터 타입이 맞지 않습니다: name={}, value={}", e.getName(), e.getValue());
        return body(HttpStatus.BAD_REQUEST, INVALID_REQUEST_MESSAGE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException e) {
        // e.getMessage() 에는 Jackson 내부 클래스명과 요청 본문 조각이 그대로 들어 있다.
        log.debug("요청 본문을 파싱하지 못했습니다.", e);
        return body(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        // Spring MVC 가 상태코드를 이미 정해서 던지는 예외들(없는 정적자원 404,
        // 허용되지 않은 메서드 405, 잘못된 Content-Type 415 ...)은 그 상태코드를 존중한다.
        // 이 분기가 없으면 전부 500 으로 뭉개진다 — 실제로 존재하지 않는 번들 파일
        // 요청이 404 가 아니라 500 으로 응답되고 있었다.
        //
        // ErrorResponse 는 인터페이스라 @ExceptionHandler 로 직접 잡을 수 없어
        // (Throwable 이 아니다) 여기서 instanceof 로 분기한다.
        if (e instanceof ErrorResponse errorResponse) {
            return handleFrameworkError(errorResponse, e);
        }

        log.error("처리되지 않은 예외가 발생했습니다.", e);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, GENERIC_SERVER_MESSAGE);
    }

    /**
     * 프레임워크가 상태코드를 정해준 예외의 응답을 만든다.
     *
     * <p>예전엔 {@code errorResponse.getBody().getDetail()} 을 그대로 사용자에게 보냈다.
     * 실측된 결과가 이랬다:
     * <pre>
     * GET    /api/v1/capsule/list   → "Required parameter 'email' is not present."
     * DELETE /api/v1/capsule/public → "Method 'DELETE' is not supported."
     * GET    /api/v1/capsule/xxx    → "No static resource api/v1/capsule/xxx."
     * </pre>
     * 영어라 사용자가 못 읽는 것도 문제지만, 내부 파라미터명 · 리소스 경로 ·
     * Content-Type 협상 결과 같은 서버 내부 계약이 그대로 새어 나간다.
     * {@code detail} 은 <b>로그에만</b> 남기고, 사용자에게는 상태코드별 고정 문구를 준다.
     */
    private ResponseEntity<Map<String, Object>> handleFrameworkError(ErrorResponse errorResponse, Exception e) {
        HttpStatusCode status = errorResponse.getStatusCode();
        String detail = errorResponse.getBody().getDetail();

        if (status.is4xxClientError()) {
            if (status.value() == HttpStatus.BAD_REQUEST.value()
                    || status.value() == HttpStatus.NOT_FOUND.value()) {
                // 400/404 는 봇 스캔과 오래된 링크가 상시로 만들어낸다.
                // WARN 이상으로 남기면 로그가 잡음으로 가득 차 쓸모없어진다.
                log.debug("클라이언트 오류 {}: {}", status.value(), detail);
            } else {
                // 405/415 등은 정상적인 브라우징으로는 나오지 않는다.
                // 프론트-백엔드 계약이 어긋났다는 신호이므로 보이게 남긴다.
                log.warn("클라이언트 오류 {}: {}", status.value(), detail);
            }
            return body(status, clientMessage(status));
        }

        // ErrorResponse 인데 5xx 인 경우(예: 비동기 요청 타임아웃 503).
        // 상태코드는 프레임워크 판단을 존중하되 문구는 일반화한다.
        log.error("프레임워크가 {} 로 처리한 예외입니다: {}", status.value(), detail, e);
        return body(status, GENERIC_SERVER_MESSAGE);
    }

    /**
     * 상태코드 → 사용자에게 보여줄 고정 한국어 문구.
     *
     * <p>여기 없는 4xx 는 기본 문구로 떨어진다. 실제로 발생할 수 없는 상태코드까지
     * 미리 문구를 지어내지 않는다 — 검증할 수 없는 문구는 유지보수 부채일 뿐이다.
     */
    private String clientMessage(HttpStatusCode status) {
        if (status.value() == HttpStatus.BAD_REQUEST.value()) {
            return INVALID_REQUEST_MESSAGE;
        }
        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            return "요청하신 경로를 찾을 수 없습니다.";
        }
        if (status.value() == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            return "지원하지 않는 요청 방식입니다.";
        }
        if (status.value() == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
            return "지원하지 않는 형식의 요청입니다.";
        }
        return "요청을 처리할 수 없습니다.";
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatusCode status, String message) {
        Map<String, Object> response = new HashMap<>();
        // error / message 두 키를 모두 채운다 — 프론트가 두 이름을 섞어 쓴다.
        response.put("error", message);
        response.put("message", message);
        response.put("status", status.value());
        return ResponseEntity.status(status).body(response);
    }
}
