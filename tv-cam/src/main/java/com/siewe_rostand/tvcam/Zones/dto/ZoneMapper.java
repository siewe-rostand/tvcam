package com.siewe_rostand.tvcam.Zones.dto;

import com.siewe_rostand.tvcam.Zones.model.Zone;
import com.siewe_rostand.tvcam.Zones.model.UserZone;
import org.springframework.stereotype.Component;

/**
 * Mapper class for Zone entity conversions
 * 
 * @author rostand
 * @project tv-cam
 */
@Component
public class ZoneMapper {

    public Zone toZone(ZoneRequest request) {
        return Zone.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    public ZoneResponse toResponse(Zone zone) {
        return ZoneResponse.builder()
                .id(zone.getZoneId())
                .name(zone.getName())
                .code(zone.getCode())
                .description(zone.getDescription())
                .isActive(zone.getIsActive())
                .createdAt(zone.getCreatedAt() != null ? zone.getCreatedAt().toString() : null)
                .updatedAt(zone.getUpdatedAt() != null ? zone.getUpdatedAt().toString() : null)
                .build();
    }

    public UserZoneResponse toUserZoneResponse(UserZone userZone) {
        return UserZoneResponse.builder()
                .id(userZone.getId())
                .userId(userZone.getUser().getUserId())
                .userFullName(userZone.getUser().getFirstname() + " " + userZone.getUser().getLastname())
                .userTelephone(userZone.getUser().getTelephone())
                .zoneId(userZone.getZone().getZoneId())
                .zoneName(userZone.getZone().getName())
                .zoneCode(userZone.getZone().getCode())
                .isPrimaryZone(userZone.getIsPrimaryZone())
                .assignedDate(userZone.getAssignedDate())
                .isActive(userZone.getIsActive())
                .createdAt(userZone.getCreatedAt() != null ? userZone.getCreatedAt().toString() : null)
                .build();
    }
}