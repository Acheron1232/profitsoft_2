package com.acheron.profitsoft2.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<ErrorMessage> handleConflict(Exception ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST,path));
    }
    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(Exception ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST,path));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            WebRequest request) {

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                new ErrorMessage(
                        String.join("; ", errors),
                        HttpStatus.BAD_REQUEST,
                        ((ServletWebRequest) request).getRequest().getRequestURI()
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex,
            WebRequest request
    ) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        String message = "Data integrity violation";

        if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("duplicate key")) {
            message = "Duplicate value violates unique constraint: " + ex.getRootCause().getMessage();
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(message, HttpStatus.CONFLICT, path));
    }

}
