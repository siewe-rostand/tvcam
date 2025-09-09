package com.siewe_rostand.tvcam.Issue.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.Issue.dto.IssueRequest;
import com.siewe_rostand.tvcam.Issue.dto.IssueResponse;
import com.siewe_rostand.tvcam.Issue.service.IssueService;
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
 * Contrôleur REST pour la gestion des réclamations/incidents
 *
 * @author rostand
 * @project tv-cam
 */
@RestController
@RequestMapping("/issues")
@RequiredArgsConstructor
@Tag(name = "Issue Management", description = "API pour la gestion des réclamations et incidents")
public class IssueController {

    private final IssueService issueService;

    @Operation(summary = "Créer une nouvelle réclamation")
    @PostMapping
    public ResponseEntity<HttpResponse<Object>> createIssue(@RequestBody IssueRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        IssueResponse response = issueService.createIssue(request);
        Map<String, Object> data = objectMapper.convertValue(response, new TypeReference<>() {
        });

        return ResponseEntity.created(URI.create("/issues/" + response.getIssueId()))
                .body(HttpResponse.builder()
                        .timestamp(now()).success(true)
                        .message("Réclamation créée avec succès")
                        .status(CREATED.getReasonPhrase())
                        .statusCode(CREATED.value())
                        .data(data)
                        .build());
    }

    @Operation(summary = "Récupérer toutes les réclamations avec pagination et filtres")
    @GetMapping
    public ResponseEntity<PaginatedResponse> getAllIssues(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "search", defaultValue = "") String searchTerm,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String issueType,
            @RequestParam(name = "priority", required = false) String priority) {

        PaginatedResponse response = issueService.getAllIssues(page, size, sortBy, direction, searchTerm, status,
                issueType, priority);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer une réclamation par ID")
    @GetMapping("/{issueId}")
    public ResponseEntity<HttpResponse<Object>> getIssueById(@PathVariable Long issueId) {
        ObjectMapper objectMapper = new ObjectMapper();
        IssueResponse response = issueService.getIssueById(issueId);
        Map<String, Object> data = objectMapper.convertValue(response, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now()).success(true)
                .message("Réclamation récupérée avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .data(data)
                .build());
    }

    @Operation(summary = "Récupérer les réclamations d'un client")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<HttpResponse<Object>> getIssuesByCustomer(@PathVariable Long customerId) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<IssueResponse> issues = issueService.getIssuesByCustomer(customerId);
        Map<String, Object> data = objectMapper.convertValue(issues, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now()).success(true)
                .message("Réclamations du client récupérées avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .data(data)
                .build());
    }

    @Operation(summary = "Récupérer les réclamations assignées à un utilisateur")
    @GetMapping("/assigned/{userId}")
    @PreAuthorize("hasRole('TECHNICIEN') or hasRole('CHEF_CABLEUR') or hasRole('ADMIN')")
    public ResponseEntity<HttpResponse<Object>> getIssuesByAssignee(@PathVariable Long userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<IssueResponse> issues = issueService.getIssuesByAssignee(userId);
        Map<String, Object> data = objectMapper.convertValue(issues, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now()).success(true)
                .message("Réclamations assignées récupérées avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .data(data)
                .build());
    }

    @Operation(summary = "Mettre à jour une réclamation")
    @PutMapping("/{issueId}")
    @PreAuthorize("hasRole('TECHNICIEN') or hasRole('CHEF_CABLEUR') or hasRole('ADMIN')")
    public ResponseEntity<HttpResponse<Object>> updateIssue(@PathVariable Long issueId, @RequestBody IssueRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        IssueResponse response = issueService.updateIssue(issueId, request);
        Map<String, Object> data = objectMapper.convertValue(response, new TypeReference<>() {
        });

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now()).success(true)
                .message("Réclamation mise à jour avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .data(data)
                .build());
    }

    @Operation(summary = "Assigner une réclamation à un technicien")
    @PutMapping("/{issueId}/assign/{userId}")
    @PreAuthorize("hasRole('CHEF_CABLEUR') or hasRole('ADMIN')")
    public ResponseEntity<HttpResponse<Object>> assignIssue(@PathVariable Long issueId, @PathVariable Long userId) {
        HttpResponse<Object> response = issueService.assignIssue(issueId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Résoudre une réclamation")
    @PutMapping("/{issueId}/resolve")
    @PreAuthorize("hasRole('TECHNICIEN') or hasRole('CHEF_CABLEUR') or hasRole('ADMIN')")
    public ResponseEntity<HttpResponse<Object>> resolveIssue(@PathVariable Long issueId,
                                                             @RequestBody Map<String, String> resolution) {
        HttpResponse<Object> response = issueService.resolveIssue(issueId, resolution.get("resolution"));
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Fermer une réclamation")
    @PutMapping("/{issueId}/close")
    @PreAuthorize("hasRole('CHEF_CABLEUR') or hasRole('ADMIN')")
    public ResponseEntity<HttpResponse<Object>> closeIssue(@PathVariable Long issueId) {
        HttpResponse<Object> response = issueService.closeIssue(issueId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Supprimer une réclamation")
    @DeleteMapping("/{issueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpResponse<Object>> deleteIssue(@PathVariable Long issueId) {
        HttpResponse<Object> response = issueService.deleteIssue(issueId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Évaluer le service après résolution")
    @PutMapping("/{issueId}/feedback")
    public ResponseEntity<HttpResponse<Object>> provideFeedback(@PathVariable Long issueId,
                                                                @RequestBody Map<String, Object> feedback) {
        Integer rating = (Integer) feedback.get("rating");
        String comment = (String) feedback.get("comment");
        HttpResponse<Object> response = issueService.provideFeedback(issueId, rating, comment);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Récupérer les statistiques des réclamations")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('CHEF_CABLEUR') or hasRole('ADMIN')")
    public ResponseEntity<HttpResponse<Object>> getIssueStatistics() {
        Map<String, Object> statistics = issueService.getIssueStatistics();

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now()).success(true)
                .message("Statistiques récupérées avec succès")
                .status(OK.getReasonPhrase())
                .statusCode(OK.value())
                .data(statistics)
                .build());
    }
}
