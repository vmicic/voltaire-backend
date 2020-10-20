package com.voltaire.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ApiException {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private String path;

}
