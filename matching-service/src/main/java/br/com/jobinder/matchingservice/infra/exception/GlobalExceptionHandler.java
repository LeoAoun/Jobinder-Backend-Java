package br.com.jobinder.matchingservice.infra.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle not found error when a match is not found
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleMatchNotFound(MatchNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    // Handle conflict error when trying to create a match that already exists
    @ExceptionHandler(MatchAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleMatchAlreadyExists(MatchAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    // Handle 404 from Feign client (e.g., user or profile not found in identity-service)
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Map<String, String>> handleFeignNotFound(FeignException.NotFound ex) {
        System.out.println("FeignException.NotFound: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Could not find the specified user or profile to create a match."));
    }

    // Handle other Feign exceptions
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleGenericFeignException(FeignException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error communicating with the identity service. Please try again later."));
    }

}
