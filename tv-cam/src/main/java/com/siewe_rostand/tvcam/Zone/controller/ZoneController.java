package com.siewe_rostand.tvcam.Zone.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.Zone.dto.ZoneRequest;
import com.siewe_rostand.tvcam.Zone.dto.ZoneResponse;
import com.siewe_rostand.tvcam.Zone.service.ZoneService;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Contrôleur REST pour la gestion des zones
 * 
 * @author rostand
 * @project tv-cam
 */
@RestController
@RequestMapping("/zones")
@RequiredArgsConstructor
@Tag(name = "Zone Management", description = "API pour la gestion des zones géographiques")
public class ZoneController {

    private final ZoneService zoneService;

    @Operation(summary = "Créer une nouvelle zone")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> createZone(@RequestBody ZoneRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ZoneResponse response = zoneService.createZone(request);
        Map<String, Object> data = objectMapper.convertValue(response, new TypeReference<>() {
        });

        return ResponseEntity.created(URI.create("/zones/" + response.getZoneId()))
                .body(HttpResponse.builder()
                        .timestamp(now())
                        .message("Zone créée avec succès")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .data(data)
                        .build());
    }

    @Operation(summary = "Récupérer toutes les zones avec pagination")
    @GetMapping
    public ResponseEntity<PaginatedResponse> getAllZones(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "search", defaultValue = "") String searchTerm) {

        PaginatedResponse response = zoneService.getAllZones(page, size, sortBy, direction, searchTerm);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer les zones actives")
    @GetMapping("/active")
    public ResponseEntity<HttpResponse> getActiveZones() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ZoneResponse> zones = zoneService.getActiveZones();
        Map<String, Object> data = objectMapper.convertValue(zones, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Zones actives récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(data)
                .build());
    }

    @Operation(summary = "Récupérer une zone par ID")
    @GetMapping("/{zoneId}")
    public ResponseEntity<HttpResponse> getZoneById(@PathVariable Long zoneId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ZoneResponse response = zoneService.getZoneById(zoneId);
        Map<String, Object> data = objectMapper.convertValue(response, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Zone récupérée avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(data)
                .build());
    }

    @Operation(summary = "Mettre à jour une zone")
    @PutMapping("/{zoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> updateZone(@PathVariable Long zoneId, @RequestBody ZoneRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ZoneResponse response = zoneService.updateZone(zoneId, request);
        Map<String, Object> data = objectMapper.convertValue(response, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Zone mise à jour avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(data)
                .build());
    }

    @Operation(summary = "Supprimer une zone")
    @DeleteMapping("/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpResponse> deleteZone(@PathVariable Long zoneId) {
        HttpResponse response = zoneService.deleteZone(zoneId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Assigner un utilisateur à une zone")
    @PostMapping("/{zoneId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> assignUserToZone(@PathVariable Long zoneId, @PathVariable Long userId) {
        HttpResponse response = zoneService.assignUserToZone(zoneId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Retirer un utilisateur d'une zone")
    @DeleteMapping("/{zoneId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> removeUserFromZone(@PathVariable Long zoneId, @PathVariable Long userId) {
        HttpResponse response = zoneService.removeUserFromZone(zoneId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Assigner un client à une zone")
    @PostMapping("/{zoneId}/customers/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CHEF_CABLEUR')")
    public ResponseEntity<HttpResponse> assignCustomerToZone(@PathVariable Long zoneId, @PathVariable Long customerId) {
        HttpResponse response = zoneService.assignCustomerToZone(zoneId, customerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer les zones d'un utilisateur")
    @GetMapping("/user/{userId}")
    public ResponseEntity<HttpResponse> getUserZones(@PathVariable Long userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ZoneResponse> zones = zoneService.getZonesByUserId(userId);
        Map<String, Object> data = objectMapper.convertValue(zones, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Zones de l'utilisateur récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(data)
                .build());
    }
}
