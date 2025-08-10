package com.musinsa.category.exception;

public class CategoryNotFoundException extends NotFoundException {
    private static final String CODE = "CATEGORY_NOT_FOUND";
    private static final String MESSAGE = "존재하지 않는 카테고리입니다.";

    public CategoryNotFoundException() {
        super(CODE, MESSAGE);
    }
}