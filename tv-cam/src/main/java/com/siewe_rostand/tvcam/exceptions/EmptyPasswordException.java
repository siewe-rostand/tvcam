package com.siewe_rostand.tvcam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author rostand
 * @project tv-cam
 */

@ResponseStatus(value = BAD_REQUEST)
public class EmptyPasswordException  extends AuthenticationException {

    public EmptyPasswordException(String msg) {
        super(msg);
    }
}
