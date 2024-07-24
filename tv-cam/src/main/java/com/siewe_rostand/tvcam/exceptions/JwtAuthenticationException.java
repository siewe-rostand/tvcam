package com.siewe_rostand.tvcam.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author rostand
 * @project tv-cam
 */
public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
