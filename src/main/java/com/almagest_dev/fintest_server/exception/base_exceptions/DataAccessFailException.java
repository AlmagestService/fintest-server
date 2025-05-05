package com.almagest_dev.fintest_server.exception.base_exceptions;

public class DataAccessFailException extends RuntimeException {
    public DataAccessFailException(String message) {
        super(message);
    }
}
