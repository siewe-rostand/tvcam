package com.siewe_rostand.tvcam.shared.Exceptions;

/**
 * @author rostand
 * @project tv-cam
 */
public class OperationNotPermittedException extends RuntimeException {

    public OperationNotPermittedException() {
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }

}
