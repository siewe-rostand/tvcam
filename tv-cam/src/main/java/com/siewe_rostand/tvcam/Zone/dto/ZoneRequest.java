package com.siewe_rostand.tvcam.Zone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO pour les requêtes Zone
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZoneRequest {

    @NotBlank(message = "Le nom de la zone est obligatoire")
    private String name;

    private String description;

    @NotBlank(message = "Le code de la zone est obligatoire")
    private String code;

    private Boolean isActive;

    private Double latitude;

    private Double longitude;

    private String address;
}
