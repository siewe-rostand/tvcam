package com.siewe_rostand.tvcam.Zone.dto;

import lombok.*;

/**
 * DTO pour les réponses Zone
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZoneResponse {

    private Long zoneId;
    private String name;
    private String description;
    private String code;
    private Boolean isActive;
    private Double latitude;
    private Double longitude;
    private String address;
    private String createdAt;
    private String updatedAt;
    private Integer customersCount;
    private Integer usersCount;
}
