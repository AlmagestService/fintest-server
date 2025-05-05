package com.almagest_dev.fintest_server.exception.base_exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
