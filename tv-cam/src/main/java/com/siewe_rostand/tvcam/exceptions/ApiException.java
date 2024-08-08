package com.siewe_rostand.tvcam.exceptions;

public class ApiException extends RuntimeException {
    String reason;
    public ApiException(String message) { super(message); }

    public ApiException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}