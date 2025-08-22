package com.siewe_rostand.tvcam.Zones.service;

import com.siewe_rostand.tvcam.Zones.dto.*;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

/**
 * Service interface for Zone management
 * 
 * @author rostand
 * @project tv-cam
 */
public interface ZoneService {
    
    ZoneResponse createZone(ZoneRequest request);
    
    ZoneResponse updateZone(Long zoneId, ZoneRequest request);
    
    ZoneResponse getZoneById(Long zoneId);
    
    PaginatedResponse getAllZones(Integer page, Integer size, String sortBy, String direction, String search);
    
    List<ZoneResponse> getActiveZones();
    
    void deactivateZone(Long zoneId);
    
    void activateZone(Long zoneId);
    
    List<UserZoneResponse> assignUserToZones(UserZoneAssignmentRequest request);
    
    void removeUserFromZone(Long userId, Long zoneId);
    
    List<UserZoneResponse> getUserZones(Long userId);
    
    List<UserZoneResponse> getZoneUsers(Long zoneId);
    
    void setPrimaryZone(Long userId, Long zoneId);
    
    boolean canUserAccessZone(Long userId, Long zoneId);
    
    List<Long> getUserAccessibleZoneIds(Long userId);
}