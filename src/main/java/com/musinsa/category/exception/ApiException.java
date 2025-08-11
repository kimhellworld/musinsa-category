package com.musinsa.category.exception;


import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
  private final String errorCode;
  private final String errorMessage;

  public ApiException() {
    super();
    this.errorCode = null;
    this.errorMessage = null;
  }

  public ApiException(String errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
