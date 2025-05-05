package com.almagest_dev.fintest_server.exception;

public class ApiRequestFailException extends RuntimeException{
    public ApiRequestFailException(String message) {
        super(message);
    }
}
