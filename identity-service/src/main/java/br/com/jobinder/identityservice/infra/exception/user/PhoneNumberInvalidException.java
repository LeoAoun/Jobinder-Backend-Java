package br.com.jobinder.identityservice.infra.exception.user;

public class PhoneNumberInvalidException extends IllegalArgumentException {
    public PhoneNumberInvalidException(String message) {
        super(message);
    }
}