package com.siewe_rostand.tvcam.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author rostand
 * @project tv-cam
 */
public class JwtAuthenticationException extends AuthenticationException {
    String reason;

    public JwtAuthenticationException(String msg, String reason) {
        super(msg);
        this.reason = reason;
    }
}
