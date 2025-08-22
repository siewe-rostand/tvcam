package com.siewe_rostand.tvcam.Issues.service;

import com.siewe_rostand.tvcam.Issues.dto.*;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;

/**
 * Service interface for Issue management
 * 
 * @author rostand
 * @project tv-cam
 */
public interface IssueService {
    
    IssueResponse createIssue(IssueRequest request);
    
    IssueResponse updateIssue(Long issueId, IssueRequest request);
    
    IssueResponse getIssueById(Long issueId);
    
    IssueResponse getIssueByReference(String reference);
    
    PaginatedResponse getAllIssues(Integer page, Integer size, String sortBy, String direction, String search);
    
    PaginatedResponse getIssuesByZone(Long zoneId, Integer page, Integer size, String sortBy, String direction, String search);
    
    PaginatedResponse getIssuesForUser(Long userId, Integer page, Integer size, String sortBy, String direction, String search);
    
    List<IssueResponse> getCustomerIssues(Long customerId);
    
    List<IssueResponse> getTechnicianAssignedIssues(Long technicianId);
    
    IssueResponse updateIssueStatus(IssueStatusUpdateRequest request);
    
    IssueResponse assignTechnician(Long issueId, Long technicianId);
    
    void escalateIssue(Long issueId, String escalationReason);
    
    // Issue Comments
    IssueCommentResponse addComment(IssueCommentRequest request);
    
    PaginatedResponse getIssueComments(Long issueId, Integer page, Integer size, Boolean includeInternal);
    
    // Statistics
    IssueStatsResponse getIssueStats(Long userId);
    
    IssueStatsResponse getZoneIssueStats(Long zoneId);
    
    List<IssueResponse> getOverdueIssues();
}