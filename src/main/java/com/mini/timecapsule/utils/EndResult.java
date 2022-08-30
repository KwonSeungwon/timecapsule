package com.mini.timecapsule.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class EndResult extends UObject {
    private int code;
    private String message;

    protected EndResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonIgnore
    public int getStatus() {
        return code / 1000;
    }

    public static EndResult of(int code, String message) {
        return new EndResult(code, message);
    }
}


