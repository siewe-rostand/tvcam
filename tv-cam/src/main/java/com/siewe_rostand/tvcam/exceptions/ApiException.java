package com.siewe_rostand.tvcam.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND)
public class ApiException extends RuntimeException {
    String reason;
    public ApiException(String message) { super(message); }

    public ApiException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}