package com.musinsa.category.exception;

public class BadRequestException extends ApiException {

    public BadRequestException() {
        super();
    }

    public BadRequestException(String code, String message) {
        super(code, message);
    }
}