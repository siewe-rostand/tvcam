package com.siewe_rostand.tvcam.Issue.dto;

import com.siewe_rostand.tvcam.Issue.model.Issue;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Issue et DTOs
 * 
 * @author rostand
 * @project tv-cam
 */
@Component
public class IssueMapper {

    public Issue toIssue(IssueRequest request) {
        if (request == null) {
            return null;
        }

        Issue issue = new Issue();
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setIssueType(request.getIssueType());
        issue.setPriority(request.getPriority());
        issue.setDueDate(request.getDueDate());
        issue.setNotes(request.getNotes());

        return issue;
    }

    public IssueResponse toResponse(Issue issue) {
        if (issue == null) {
            return null;
        }

        return IssueResponse.builder()
                .issueId(issue.getIssueId())
                .referenceNumber(issue.getReferenceNumber())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .issueType(issue.getIssueType())
                .priority(issue.getPriority())
                .status(issue.getStatus())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .dueDate(issue.getDueDate())
                .resolvedAt(issue.getResolvedAt())
                .resolution(issue.getResolution())
                .notes(issue.getNotes())
                .customerRating(issue.getCustomerRating())
                .customerFeedback(issue.getCustomerFeedback())
                .customerId(issue.getCustomer() != null ? issue.getCustomer().getCustomerId() : null)
                .customerName(issue.getCustomer() != null ? issue.getCustomer().getName() : null)
                .assignedToId(issue.getAssignedTo() != null ? issue.getAssignedTo().getUserId() : null)
                .assignedToName(issue.getAssignedTo() != null
                        ? issue.getAssignedTo().getFirstname() + " " + issue.getAssignedTo().getLastname()
                        : null)
                .build();
    }
}
