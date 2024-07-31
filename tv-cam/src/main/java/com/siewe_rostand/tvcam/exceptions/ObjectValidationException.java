package com.siewe_rostand.tvcam.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author rostand
 * @project tv-cam
 */

@RequiredArgsConstructor
public class ObjectValidationException extends RuntimeException {

    @Getter
    private final Set<String> violations;
    @Getter
    private final String violationSource;
}
