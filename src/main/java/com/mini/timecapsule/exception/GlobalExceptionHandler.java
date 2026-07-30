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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());
        response.put("message", e.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청값이 올바르지 않습니다.");

        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("message", message);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());
        response.put("message", e.getMessage());
        response.put("status", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException e) {
        String message = "요청 형식이 올바르지 않습니다.";
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("message", message);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
            HttpStatusCode status = errorResponse.getStatusCode();
            String detail = errorResponse.getBody().getDetail();
            String message = detail != null ? detail : String.valueOf(status);

            Map<String, Object> response = new HashMap<>();
            response.put("error", message);
            response.put("message", message);
            response.put("status", status.value());
            return ResponseEntity.status(status).body(response);
        }

        log.error("처리되지 않은 예외가 발생했습니다.", e);

        String message = "서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.";
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("message", message);
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
