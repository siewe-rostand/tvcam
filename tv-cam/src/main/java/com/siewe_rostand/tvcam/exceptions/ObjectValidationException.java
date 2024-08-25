package com.siewe_rostand.tvcam.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@RequiredArgsConstructor
public class ObjectValidationException extends RuntimeException {

    private final Set<String> violations;
    private final String violationSource;
    private final String reason;
}
