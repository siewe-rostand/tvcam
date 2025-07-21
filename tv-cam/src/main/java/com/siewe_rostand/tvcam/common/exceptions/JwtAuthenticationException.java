package com.siewe_rostand.tvcam.common.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author rostand
 * @project tv-cam
 */
public class JwtAuthenticationException extends AuthenticationException {
    public String reason;

    public JwtAuthenticationException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public JwtAuthenticationException(String msg, Throwable cause, String reason) {
        super(msg, cause);
        this.reason = reason;
    }
}
