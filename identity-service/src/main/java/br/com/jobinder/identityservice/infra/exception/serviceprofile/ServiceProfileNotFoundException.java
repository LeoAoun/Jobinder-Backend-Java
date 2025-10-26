package br.com.jobinder.identityservice.infra.exception.serviceprofile;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceProfileNotFoundException extends RuntimeException {
    public ServiceProfileNotFoundException(String message) {
        super(message);
    }
}
