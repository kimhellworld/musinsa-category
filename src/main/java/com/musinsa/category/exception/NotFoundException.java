package com.musinsa.category.exception;

public class NotFoundException extends ApiException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String code, String message) {
        super(code, message);
    }
}