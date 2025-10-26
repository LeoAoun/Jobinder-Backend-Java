package br.com.jobinder.matchingservice.infra.exception;

public class MatchAlreadyExistsException extends RuntimeException {
    public MatchAlreadyExistsException(String message) {
        super(message);
    }
}