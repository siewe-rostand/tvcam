package com.siewe_rostand.tvcam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author rostand
 * @project tv-cam
 */

@ResponseStatus(value = NOT_FOUND)
public class ResourceNotFoundException extends Exception{

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message){
        super(message);
    }
}
