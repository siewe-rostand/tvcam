package com.siewe_rostand.tvcam.Zone.dto;

import com.siewe_rostand.tvcam.Zone.model.Zone;
import com.siewe_rostand.tvcam.Zone.service.ZoneCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Zone et DTOs
 *
 * @author rostand
 * @project tv-cam
 */
@Component
@RequiredArgsConstructor
public class ZoneMapper {

    private final ZoneCodeGenerator zoneCodeGenerator;

    public Zone toZone(ZoneRequest request) {
        if (request == null) {
            return null;
        }

        return Zone.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .code(zoneCodeGenerator.generateZoneCodeFromName(request.getName()))
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    public ZoneResponse toResponse(Zone zone) {
        if (zone == null) {
            return null;
        }

        return ZoneResponse.builder()
                .zoneId(zone.getZoneId())
                .name(zone.getName())
                .description(zone.getDescription())
                .code(zone.getCode())
                .address(zone.getAddress())
                .isActive(zone.getIsActive())
                .createdAt(zone.getCreatedAt() != null ? zone.getCreatedAt() : null)
                .updatedAt(zone.getUpdatedAt() != null ? zone.getUpdatedAt() : null)
                .usersCount(zone.getUsers() != null ? zone.getUsers().size() : 0)
                .customersCount(zone.getCustomers() != null ? zone.getCustomers().size() : 0)
                .build();
    }

    public void updateZoneFromRequest(ZoneRequest request, Zone zone) {
        if (request == null || zone == null) {
            return;
        }

        if (request.getName() != null) {
            zone.setName(request.getName());
        }
        if (request.getDescription() != null) {
            zone.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            zone.setIsActive(request.getIsActive());
        }
        if (request.getAddress() != null) {
            zone.setAddress(request.getAddress());
        }
    }
}
