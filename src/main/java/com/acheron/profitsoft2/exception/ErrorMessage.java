package com.acheron.profitsoft2.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorMessage {
    private final Instant instant = Instant.now();
    private String message;
    private HttpStatus status;
    private String path;
}
