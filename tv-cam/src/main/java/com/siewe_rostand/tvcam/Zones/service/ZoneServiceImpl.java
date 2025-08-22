package com.siewe_rostand.tvcam.Zones.service;

import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.Users.UsersRepository;
import com.siewe_rostand.tvcam.Zones.dto.*;
import com.siewe_rostand.tvcam.Zones.model.UserZone;
import com.siewe_rostand.tvcam.Zones.model.Zone;
import com.siewe_rostand.tvcam.Zones.repository.UserZoneRepository;
import com.siewe_rostand.tvcam.Zones.repository.ZoneRepository;
import com.siewe_rostand.tvcam.exceptions.ApiException;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ZoneService
 * 
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final UserZoneRepository userZoneRepository;
    private final UsersRepository usersRepository;
    private final ZoneMapper zoneMapper;

    @Override
    public ZoneResponse createZone(ZoneRequest request) {
        log.info("Creating new zone with code: {}", request.getCode());
        
        if (zoneRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new ApiException("Zone with code " + request.getCode() + " already exists");
        }
        
        if (zoneRepository.existsByNameAndIsActiveTrue(request.getName())) {
            throw new ApiException("Zone with name " + request.getName() + " already exists");
        }
        
        Zone zone = zoneMapper.toZone(request);
        Zone savedZone = zoneRepository.save(zone);
        
        log.info("Zone created successfully with ID: {}", savedZone.getZoneId());
        return zoneMapper.toResponse(savedZone);
    }

    @Override
    public ZoneResponse updateZone(Long zoneId, ZoneRequest request) {
        log.info("Updating zone with ID: {}", zoneId);
        
        Zone existingZone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        // Check if code is unique (excluding current zone)
        if (!existingZone.getCode().equals(request.getCode()) && 
            zoneRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new ApiException("Zone with code " + request.getCode() + " already exists");
        }
        
        // Check if name is unique (excluding current zone)
        if (!existingZone.getName().equals(request.getName()) && 
            zoneRepository.existsByNameAndIsActiveTrue(request.getName())) {
            throw new ApiException("Zone with name " + request.getName() + " already exists");
        }
        
        existingZone.setName(request.getName());
        existingZone.setCode(request.getCode());
        existingZone.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            existingZone.setIsActive(request.getIsActive());
        }
        
        Zone updatedZone = zoneRepository.save(existingZone);
        log.info("Zone updated successfully with ID: {}", updatedZone.getZoneId());
        
        return zoneMapper.toResponse(updatedZone);
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneResponse getZoneById(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        return zoneMapper.toResponse(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse getAllZones(Integer page, Integer size, String sortBy, String direction, String search) {
        log.info("Fetching zones with page: {}, size: {}, search: {}", page, size, search);
        
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<Zone> zonePage;
        
        if (search != null && !search.trim().isEmpty()) {
            zonePage = zoneRepository.findZonesWithSearch(search.trim(), true, pageable);
        } else {
            zonePage = zoneRepository.findAll(pageable);
        }
        
        List<ZoneResponse> zones = zonePage.getContent().stream()
                .map(zoneMapper::toResponse)
                .collect(Collectors.toList());
        
        return PaginatedResponse.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Zones retrieved successfully")
                .data(zones)
                .lastPage(zonePage.isLast())
                .firstPage(zonePage.isFirst())
                .empty(zonePage.isEmpty())
                .sorted(zonePage.getSort().isSorted())
                .totalPages(zonePage.getTotalPages())
                .totalElements((int) zonePage.getTotalElements())
                .numberOfElements(zonePage.getNumberOfElements())
                .page(zonePage.getNumber())
                .paged(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getActiveZones() {
        List<Zone> activeZones = zoneRepository.findAllByIsActiveTrue();
        return activeZones.stream()
                .map(zoneMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivateZone(Long zoneId) {
        log.info("Deactivating zone with ID: {}", zoneId);
        
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        zone.setIsActive(false);
        zoneRepository.save(zone);
        
        log.info("Zone deactivated successfully with ID: {}", zoneId);
    }

    @Override
    public void activateZone(Long zoneId) {
        log.info("Activating zone with ID: {}", zoneId);
        
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        zone.setIsActive(true);
        zoneRepository.save(zone);
        
        log.info("Zone activated successfully with ID: {}", zoneId);
    }

    @Override
    public List<UserZoneResponse> assignUserToZones(UserZoneAssignmentRequest request) {
        log.info("Assigning user {} to zones: {}", request.getUserId(), request.getZoneIds());
        
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new ApiException("User not found with ID: " + request.getUserId()));
        
        List<UserZone> userZones = new ArrayList<>();
        
        for (Long zoneId : request.getZoneIds()) {
            Zone zone = zoneRepository.findById(zoneId)
                    .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
            
            // Check if assignment already exists
            if (!userZoneRepository.existsByUserAndZoneAndIsActiveTrue(user, zone)) {
                UserZone userZone = UserZone.builder()
                        .user(user)
                        .zone(zone)
                        .isPrimaryZone(false)
                        .assignedDate(request.getAssignedDate())
                        .isActive(true)
                        .build();
                
                userZones.add(userZoneRepository.save(userZone));
            }
        }
        
        // Set primary zone if specified
        if (request.getPrimaryZoneId() != null) {
            setPrimaryZone(request.getUserId(), request.getPrimaryZoneId());
        }
        
        log.info("User assigned to {} zones successfully", userZones.size());
        
        // Return all user zones (including existing ones)
        return getUserZones(request.getUserId());
    }

    @Override
    public void removeUserFromZone(Long userId, Long zoneId) {
        log.info("Removing user {} from zone {}", userId, zoneId);
        
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
        
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        UserZone userZone = userZoneRepository.findByUserAndZoneAndIsActiveTrue(user, zone)
                .orElseThrow(() -> new ApiException("User is not assigned to this zone"));
        
        userZone.setIsActive(false);
        userZoneRepository.save(userZone);
        
        log.info("User removed from zone successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserZoneResponse> getUserZones(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
        
        List<UserZone> userZones = userZoneRepository.findAllByUserAndIsActiveTrue(user);
        return userZones.stream()
                .map(zoneMapper::toUserZoneResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserZoneResponse> getZoneUsers(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        List<UserZone> userZones = userZoneRepository.findAllByZoneAndIsActiveTrue(zone);
        return userZones.stream()
                .map(zoneMapper::toUserZoneResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void setPrimaryZone(Long userId, Long zoneId) {
        log.info("Setting primary zone {} for user {}", zoneId, userId);
        
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
        
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        // Verify user is assigned to this zone
        UserZone userZone = userZoneRepository.findByUserAndZoneAndIsActiveTrue(user, zone)
                .orElseThrow(() -> new ApiException("User is not assigned to this zone"));
        
        // Reset all primary zones for this user
        userZoneRepository.resetPrimaryZoneForUser(user);
        
        // Set new primary zone
        userZone.setIsPrimaryZone(true);
        userZoneRepository.save(userZone);
        
        log.info("Primary zone set successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessZone(Long userId, Long zoneId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
        
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ApiException("Zone not found with ID: " + zoneId));
        
        return userZoneRepository.existsByUserAndZoneAndIsActiveTrue(user, zone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUserAccessibleZoneIds(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
        
        List<Zone> zones = userZoneRepository.findZonesByUser(user);
        return zones.stream()
                .map(Zone::getZoneId)
                .collect(Collectors.toList());
    }

    private Pageable createPageable(Integer page, Integer size, String sortBy, String direction) {
        page = (page != null && page >= 0) ? page : 0;
        size = (size != null && size > 0) ? size : 10;
        sortBy = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "name";
        
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        }
        
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}