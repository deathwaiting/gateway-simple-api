package com.m.ahmedgalaltask.exceptions;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;

@Getter
public class RunTimeBusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public RunTimeBusinessException(HttpStatus status, String msg, Object... params) {
        this.message = format(msg, params);
        this.status = status;
    }
}
