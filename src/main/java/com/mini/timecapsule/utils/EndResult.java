package com.mini.timecapsule.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EndResult extends UObject {
    private int code;
    private String message;

    protected EndResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonIgnore
    public int getStatus() {
        return this.code / 1000;
    }

    public static EndResult of(int code, String message) {
        return new EndResult(code, message);
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}


