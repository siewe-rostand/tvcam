package com.siewe_rostand.tvcam.Zones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for UserZone response data
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserZoneResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private String userTelephone;
    private Long zoneId;
    private String zoneName;
    private String zoneCode;
    private Boolean isPrimaryZone;
    private LocalDate assignedDate;
    private Boolean isActive;
    private String createdAt;
}