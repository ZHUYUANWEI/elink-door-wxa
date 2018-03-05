package com.ssm.common;

public class FlowException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String SYSTEM_FLOW = "SYSTEM_ERROR";
    public static final String SYSTEM_FLOW_STR = "系统异常";

    private String errorCode;

    private String errorMessage;

    public FlowException() {
        super();
    }

    public FlowException(String code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public FlowException(String code, String message, Throwable cause) {
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
        return "WL!200:"+ errorMessage;
    }

}

