package com.catarse.engine.exception;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String error;

    public ErrorResponse(int status, String message, String path, String error) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.error = error;
    }
}