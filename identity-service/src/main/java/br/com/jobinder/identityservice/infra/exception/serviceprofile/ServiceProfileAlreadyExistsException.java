package br.com.jobinder.identityservice.infra.exception.serviceprofile;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ServiceProfileAlreadyExistsException extends RuntimeException {
    public ServiceProfileAlreadyExistsException(String message) {
        super(message);
    }
}
