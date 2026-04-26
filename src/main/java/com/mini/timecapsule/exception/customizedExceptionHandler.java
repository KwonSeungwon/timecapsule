package com.mini.timecapsule.exception;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class customizedExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> NotFoundExceptionHandler() {
        return ResponseEntity.status(ErrorContent.NOT_FOUND_USER.getCode())
                .body(ErrorContent.NOT_FOUND_USER.getDescription());
    }


    public enum ErrorContent {
        NOT_FOUND_COORDINATE(404001, "COORDINATE NOT FOUND"),
        NOT_FOUND_USER(404002, "USER NOT FOUND");
        private int code;
        private String description;
        private ErrorContent(int code, String description) {
            this.code = code;
            this.description = description;
        }
        public int getCode() {
            return code;
        }
        public String getDescription() {
            return description;
        }
    }
}
