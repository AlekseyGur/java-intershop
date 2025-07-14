package ru.alexgur.intershop.system.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String message;
    private final int errorCode;
    private final long timestamp;

    public ErrorResponse(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }
}