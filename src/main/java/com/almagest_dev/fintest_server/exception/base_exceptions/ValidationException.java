package com.almagest_dev.fintest_server.exception.base_exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
