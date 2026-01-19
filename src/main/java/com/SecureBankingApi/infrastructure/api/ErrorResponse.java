package com.SecureBankingApi.infrastructure.api;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final String error;
    private final int status;
    private final String message;
    private final String path;
    private final List<String> details;

    private ErrorResponse(LocalDateTime timestamp, String error, int status, String message, String path, List<String> details) {
        this.timestamp = timestamp;
        this.error = error;
        this.status = status;
        this.message = message;
        this.path = path;
        this.details = details;
    }
    public static ErrorResponse of(int status, String error, String message, String path){
        return new ErrorResponse(
                LocalDateTime.now(),
                error,
                status,
                message,
                path,
                null
        );
    }
    public static ErrorResponse of(int status, String error, String message, String path, List<String> details){
        return new ErrorResponse(
                LocalDateTime.now(),
                error,
                status,
                message,
                path,
                details
        );
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDetails() {
        return details;
    }
}
