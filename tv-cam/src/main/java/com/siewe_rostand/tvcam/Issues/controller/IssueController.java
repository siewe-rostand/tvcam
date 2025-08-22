package com.siewe_rostand.tvcam.Issues.controller;

import com.siewe_rostand.tvcam.Issues.dto.*;
import com.siewe_rostand.tvcam.Issues.service.IssueService;
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
 * Controller for Issue management endpoints
 * 
 * @author rostand
 * @project tv-cam
 */
@RestController
@RequestMapping("/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final ObjectsValidator<IssueRequest> issueValidator;
    private final ObjectsValidator<IssueCommentRequest> commentValidator;
    private final ObjectsValidator<IssueStatusUpdateRequest> statusValidator;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody IssueRequest request) {
        issueValidator.validate(request);
        IssueResponse response = issueService.createIssue(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{issueId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN')")
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody IssueRequest request) {
        issueValidator.validate(request);
        IssueResponse response = issueService.updateIssue(issueId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{issueId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR') or hasRole('CUSTOMER')")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long issueId) {
        IssueResponse response = issueService.getIssueById(issueId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR') or hasRole('CUSTOMER')")
    public ResponseEntity<IssueResponse> getIssueByReference(@PathVariable String reference) {
        IssueResponse response = issueService.getIssueByReference(reference);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<PaginatedResponse> getAllIssues(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "reportedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search) {
        PaginatedResponse response = issueService.getAllIssues(page, size, sortBy, direction, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/zone/{zoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<PaginatedResponse> getIssuesByZone(
            @PathVariable Long zoneId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "reportedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search) {
        PaginatedResponse response = issueService.getIssuesByZone(zoneId, page, size, sortBy, direction, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<PaginatedResponse> getIssuesForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "reportedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search) {
        PaginatedResponse response = issueService.getIssuesForUser(userId, page, size, sortBy, direction, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('CUSTOMER')")
    public ResponseEntity<List<IssueResponse>> getCustomerIssues(@PathVariable Long customerId) {
        List<IssueResponse> issues = issueService.getCustomerIssues(customerId);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/technician/{technicianId}/assigned")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('TECHNICIEN')")
    public ResponseEntity<List<IssueResponse>> getTechnicianAssignedIssues(@PathVariable Long technicianId) {
        List<IssueResponse> issues = issueService.getTechnicianAssignedIssues(technicianId);
        return ResponseEntity.ok(issues);
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN')")
    public ResponseEntity<IssueResponse> updateIssueStatus(@Valid @RequestBody IssueStatusUpdateRequest request) {
        statusValidator.validate(request);
        IssueResponse response = issueService.updateIssueStatus(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{issueId}/assign/{technicianId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR')")
    public ResponseEntity<IssueResponse> assignTechnician(
            @PathVariable Long issueId,
            @PathVariable Long technicianId) {
        IssueResponse response = issueService.assignTechnician(issueId, technicianId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{issueId}/escalate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN')")
    public ResponseEntity<Void> escalateIssue(
            @PathVariable Long issueId,
            @RequestParam String reason) {
        issueService.escalateIssue(issueId, reason);
        return ResponseEntity.noContent().build();
    }

    // Issue Comments endpoints
    @PostMapping("/comments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR') or hasRole('CUSTOMER')")
    public ResponseEntity<IssueCommentResponse> addComment(@Valid @RequestBody IssueCommentRequest request) {
        commentValidator.validate(request);
        IssueCommentResponse response = issueService.addComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{issueId}/comments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR') or hasRole('CUSTOMER')")
    public ResponseEntity<PaginatedResponse> getIssueComments(
            @PathVariable Long issueId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Boolean includeInternal) {
        PaginatedResponse response = issueService.getIssueComments(issueId, page, size, includeInternal);
        return ResponseEntity.ok(response);
    }

    // Statistics endpoints
    @GetMapping("/stats/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<IssueStatsResponse> getUserIssueStats(@PathVariable Long userId) {
        IssueStatsResponse stats = issueService.getIssueStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/zone/{zoneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF') or hasRole('CABLEUR') or hasRole('TECHNICIEN') or hasRole('RECOUVREUR')")
    public ResponseEntity<IssueStatsResponse> getZoneIssueStats(@PathVariable Long zoneId) {
        IssueStatsResponse stats = issueService.getZoneIssueStats(zoneId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CABLEUR_CHEF')")
    public ResponseEntity<List<IssueResponse>> getOverdueIssues() {
        List<IssueResponse> overdueIssues = issueService.getOverdueIssues();
        return ResponseEntity.ok(overdueIssues);
    }
}