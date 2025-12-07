package br.com.jobinder.authservice.infra.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<StandardError> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<StandardError> handleFeignException(FeignException ex, HttpServletRequest request) {
        if (ex.status() == 404) {
            return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", "User not found in Identity Service", request);
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "External Error", "Error communicating with Identity Service", request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardError> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
    }

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        StandardError err = new StandardError(
                System.currentTimeMillis(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    public record StandardError(Long timestamp, Integer status, String error, String message, String path) {}
}