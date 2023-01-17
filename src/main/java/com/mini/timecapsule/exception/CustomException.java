package com.mini.timecapsule.exception;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 7324812097264422017L;

    private final String errorMessage;


    public CustomException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
