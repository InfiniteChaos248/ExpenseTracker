package com.example.myapplication.exception;

public class AppException extends Exception {

    private Integer errorCode;
    private String errorMessage;

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public AppException() {
        super();
    }

    public AppException(Integer errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
