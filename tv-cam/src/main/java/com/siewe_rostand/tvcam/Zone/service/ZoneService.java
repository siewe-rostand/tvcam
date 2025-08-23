package com.siewe_rostand.tvcam.Zone.service;

import com.siewe_rostand.tvcam.Zone.dto.ZoneRequest;
import com.siewe_rostand.tvcam.Zone.dto.ZoneResponse;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

/**
 * Interface du service Zone
 * 
 * @author rostand
 * @project tv-cam
 */
public interface ZoneService {

    ZoneResponse createZone(ZoneRequest request);

    ZoneResponse updateZone(Long zoneId, ZoneRequest request);

    HttpResponse deleteZone(Long zoneId);

    ZoneResponse getZoneById(Long zoneId);

    PaginatedResponse getAllZones(Integer page, Integer size, String sortBy, String direction, String searchTerm);

    List<ZoneResponse> getActiveZones();

    List<ZoneResponse> getZonesByUserId(Long userId);

    HttpResponse assignUserToZone(Long zoneId, Long userId);

    HttpResponse removeUserFromZone(Long zoneId, Long userId);

    HttpResponse assignCustomerToZone(Long zoneId, Long customerId);
}
