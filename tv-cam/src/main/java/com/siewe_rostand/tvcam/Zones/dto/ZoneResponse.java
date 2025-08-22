package com.siewe_rostand.tvcam.Zones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Zone response data
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    private Long userCount;
    private Long customerCount;
}