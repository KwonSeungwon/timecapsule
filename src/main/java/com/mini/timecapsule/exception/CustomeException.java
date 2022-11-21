package com.mini.timecapsule.exception;

public class CustomeException extends RuntimeException {

    private static final long serialVersionUID = 7324812097264422017L;

    private final String errorMessage;


    public CustomeException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
