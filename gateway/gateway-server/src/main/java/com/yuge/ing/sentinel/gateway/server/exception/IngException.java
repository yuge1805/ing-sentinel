package com.yuge.ing.sentinel.gateway.server.exception;

/**
 * @author: yuge
 * @date: 2024/8/22
 **/
public class IngException extends RuntimeException {

    private String errorCode;

    public IngException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
