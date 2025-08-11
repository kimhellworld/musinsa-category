package com.musinsa.category.exception;

public class CategoryCannotDeleteException extends BadRequestException {
    private static final String CODE = "CATEGORY_CANNOT_DELETE";
    private static final String MESSAGE = "카테고리를 삭제할 수 없습니다.";

    public CategoryCannotDeleteException() {
        super(CODE, MESSAGE);
    }

    public CategoryCannotDeleteException(String message) {
        super(CODE, message);
    }
    public CategoryCannotDeleteException(String code, String message) {
        super(code, message);
    }
}