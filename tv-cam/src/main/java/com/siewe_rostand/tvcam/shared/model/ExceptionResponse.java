package com.siewe_rostand.tvcam.shared.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
    private String status;
    private Integer statusCode;
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
