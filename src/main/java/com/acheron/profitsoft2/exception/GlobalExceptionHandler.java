package com.acheron.profitsoft2.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Global exception handler for REST controllers.
 * <p>
 * Handles common exceptions and maps them to meaningful HTTP responses with logging.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles EntityNotFoundException.
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with error message and HTTP 400
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFound(Exception ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.warn("Entity not found: {} at {}", ex.getMessage(), path);
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, path));
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with error message and HTTP 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(Exception ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.warn("Illegal argument: {} at {}", ex.getMessage(), path);
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, path));
    }

    /**
     * Handles validation errors (MethodArgumentNotValidException).
     *
     * @param ex      the exception
     * @param headers HTTP headers
     * @param status  HTTP status code
     * @param request the web request
     * @return ResponseEntity with concatenated field errors and HTTP 400
     */
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

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.warn("Validation failed: {} at {}", String.join("; ", errors), path);

        return ResponseEntity.badRequest().body(
                new ErrorMessage(String.join("; ", errors), HttpStatus.BAD_REQUEST, path)
        );
    }

    /**
     * Handles DataIntegrityViolationException (e.g., unique constraint violations).
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with error message and HTTP 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request
    ) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        String message = "Data integrity violation";
        if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("duplicate key")) {
            message = "Duplicate value violates unique constraint: " + ex.getRootCause().getMessage();
        }

        log.error("Data integrity violation at {}: {}", path, message);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(message, HttpStatus.CONFLICT, path));
    }
}
