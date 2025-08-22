package com.siewe_rostand.tvcam.Zones.controller;

import com.siewe_rostand.tvcam.Zones.dto.*;
import com.siewe_rostand.tvcam.Zones.service.ZoneService;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import com.siewe_rostand.tvcam.validator.ObjectsValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Zone management endpoints
 * 
 * @author rostand
 * @project tv-cam
 */
@RestController
@RequestMapping("/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;
    private final ObjectsValidator<ZoneRequest> zoneValidator;
    private final ObjectsValidator<UserZoneAssignmentRequest> assignmentValidator;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<ZoneResponse> createZone(@Valid @RequestBody ZoneRequest request) {
        zoneValidator.validate(request);
        ZoneResponse response = zoneService.createZone(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{zoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<ZoneResponse> updateZone(
            @PathVariable Long zoneId,
            @Valid @RequestBody ZoneRequest request) {
        zoneValidator.validate(request);
        ZoneResponse response = zoneService.updateZone(zoneId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{zoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<ZoneResponse> getZoneById(@PathVariable Long zoneId) {
        ZoneResponse response = zoneService.getZoneById(zoneId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<PaginatedResponse> getAllZones(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {
        PaginatedResponse response = zoneService.getAllZones(page, size, sortBy, direction, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<List<ZoneResponse>> getActiveZones() {
        List<ZoneResponse> zones = zoneService.getActiveZones();
        return ResponseEntity.ok(zones);
    }

    @PostMapping("/{zoneId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<Void> deactivateZone(@PathVariable Long zoneId) {
        zoneService.deactivateZone(zoneId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{zoneId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<Void> activateZone(@PathVariable Long zoneId) {
        zoneService.activateZone(zoneId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<List<UserZoneResponse>> assignUserToZones(
            @Valid @RequestBody UserZoneAssignmentRequest request) {
        assignmentValidator.validate(request);
        List<UserZoneResponse> response = zoneService.assignUserToZones(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/assignments/users/{userId}/zones/{zoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<Void> removeUserFromZone(
            @PathVariable Long userId,
            @PathVariable Long zoneId) {
        zoneService.removeUserFromZone(userId, zoneId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/zones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<List<UserZoneResponse>> getUserZones(@PathVariable Long userId) {
        List<UserZoneResponse> zones = zoneService.getUserZones(userId);
        return ResponseEntity.ok(zones);
    }

    @GetMapping("/{zoneId}/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<List<UserZoneResponse>> getZoneUsers(@PathVariable Long zoneId) {
        List<UserZoneResponse> users = zoneService.getZoneUsers(zoneId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/primary-zone/{zoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<Void> setPrimaryZone(
            @PathVariable Long userId,
            @PathVariable Long zoneId) {
        zoneService.setPrimaryZone(userId, zoneId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/accessible-zones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<List<Long>> getUserAccessibleZoneIds(@PathVariable Long userId) {
        List<Long> zoneIds = zoneService.getUserAccessibleZoneIds(userId);
        return ResponseEntity.ok(zoneIds);
    }
}