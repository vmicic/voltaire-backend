package com.voltaire.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiException {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private String path;

    private ApiException() {
        timestamp = LocalDateTime.now();
    }

    @Builder
    ApiException(HttpStatus status, String message, String path, String error) {
        this();
        this.status = status;
        this.message = message;
        this.path = path;
        this.error = error;
    }
}
