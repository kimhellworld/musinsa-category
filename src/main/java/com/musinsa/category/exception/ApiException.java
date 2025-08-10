package com.musinsa.category.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ApiException extends RuntimeException{
  private final String errorCode;
  private final String errorMessage;

  public ApiException(String errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
