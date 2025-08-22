package com.siewe_rostand.tvcam.Zones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Zone creation and update requests
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneRequest {

    @NotBlank(message = "Zone name is required")
    private String name;

    @NotBlank(message = "Zone code is required")
    @Size(max = 10, message = "Zone code must not exceed 10 characters")
    private String code;

    private String description;

    private Boolean isActive = true;
}