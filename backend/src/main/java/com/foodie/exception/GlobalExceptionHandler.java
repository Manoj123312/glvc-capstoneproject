package com.foodie.exception;

import java.time.Instant;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of("timestamp", Instant.now().toString(), "status", status.value(), "message", message));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(NotFoundException e) { return body(HttpStatus.NOT_FOUND, e.getMessage()); }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> bad(BadRequestException e) { return body(HttpStatus.BAD_REQUEST, e.getMessage()); }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<Map<String, Object>> auth(RuntimeException e) { return body(HttpStatus.UNAUTHORIZED, "Invalid credentials or account disabled"); }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException e) {
        return body(HttpStatus.CONFLICT, "Operation violates data constraints (duplicate or referenced record)");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage()).reduce((a, b) -> a + "; " + b).orElse("Validation failed");
        return body(HttpStatus.BAD_REQUEST, msg);
    }
}
