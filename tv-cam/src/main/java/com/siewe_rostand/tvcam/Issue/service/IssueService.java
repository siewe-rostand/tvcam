package com.siewe_rostand.tvcam.Issue.service;

import com.siewe_rostand.tvcam.Issue.dto.IssueRequest;
import com.siewe_rostand.tvcam.Issue.dto.IssueResponse;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;

import java.util.List;
import java.util.Map;

/**
 * Interface du service Issue
 * 
 * @author rostand
 * @project tv-cam
 */
public interface IssueService {

    IssueResponse createIssue(IssueRequest request);

    IssueResponse updateIssue(Long issueId, IssueRequest request);

    HttpResponse<Object> deleteIssue(Long issueId);

    IssueResponse getIssueById(Long issueId);

    PaginatedResponse getAllIssues(Integer page, Integer size, String sortBy, String direction,
            String searchTerm, String status, String issueType, String priority);

    List<IssueResponse> getIssuesByCustomer(Long customerId);

    List<IssueResponse> getIssuesByAssignee(Long userId);

    HttpResponse<Object> assignIssue(Long issueId, Long userId);

    HttpResponse<Object> resolveIssue(Long issueId, String resolution);

    HttpResponse<Object> closeIssue(Long issueId);

    HttpResponse<Object> provideFeedback(Long issueId, Integer rating, String feedback);

    Map<String, Object> getIssueStatistics();

    List<IssueResponse> getOverdueIssues();

    List<IssueResponse> getIssuesByZone(Long zoneId);
}
