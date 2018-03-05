package com.ssm.common;

public class OnTimeOutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String CONNECTION_TIMEOUT = "COLLECTION_TIMEOUT_ERROR";
    public static final String CONNECTION_TIMEOUT_STR = "连接超时异常";

    private String errorCode;

    private String errorMessage;

    public OnTimeOutException() {
        super();
    }

    public OnTimeOutException(String code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public OnTimeOutException(String code, String message, Throwable cause) {
        super(cause);
        this.errorCode = code;
        this.errorMessage = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String code) {
        this.errorCode = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return "WL!200:" + errorMessage;
    }

}

