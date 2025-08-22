package com.siewe_rostand.tvcam.Issues.dto;

import com.siewe_rostand.tvcam.Issues.model.Issue;
import com.siewe_rostand.tvcam.Issues.model.IssueComment;
import com.siewe_rostand.tvcam.Issues.model.enumeration.CommentType;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssuePriority;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssueStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper class for Issue entity conversions
 * 
 * @author rostand
 * @project tv-cam
 */
@Component
public class IssueMapper {

    public Issue toIssue(IssueRequest request) {
        IssuePriority priority = IssuePriority.MEDIUM;
        if (request.getPriority() != null) {
            try {
                priority = IssuePriority.valueOf(request.getPriority().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Keep default priority
            }
        }

        return Issue.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(priority)
                .status(IssueStatus.OPEN)
                .reportedDate(LocalDateTime.now())
                .expectedResolutionDate(request.getExpectedResolutionDate())
                .locationDetails(request.getLocationDetails())
                .build();
    }

    public IssueResponse toResponse(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getIssueId())
                .reference(issue.getReference())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus().name())
                .priority(issue.getPriority().name())
                .reportedDate(issue.getReportedDate())
                .expectedResolutionDate(issue.getExpectedResolutionDate())
                .actualResolutionDate(issue.getActualResolutionDate())
                
                // Customer information
                .customerId(issue.getCustomer().getCustomerId())
                .customerName(issue.getCustomer().getName())
                .customerTelephone(issue.getCustomer().getTelephone())
                .customerAddress(issue.getCustomer().getAddress())
                
                // Issue type information
                .issueTypeId(issue.getIssueType().getIssueTypeId())
                .issueTypeName(issue.getIssueType().getName())
                .issueTypeCode(issue.getIssueType().getCode())
                
                // Technician information
                .assignedTechnicianId(issue.getAssignedTechnician() != null ? issue.getAssignedTechnician().getUserId() : null)
                .assignedTechnicianName(issue.getAssignedTechnician() != null ? 
                    issue.getAssignedTechnician().getFirstname() + " " + issue.getAssignedTechnician().getLastname() : null)
                .assignedTechnicianTelephone(issue.getAssignedTechnician() != null ? issue.getAssignedTechnician().getTelephone() : null)
                
                // Zone information
                .zoneId(issue.getZone().getZoneId())
                .zoneName(issue.getZone().getName())
                .zoneCode(issue.getZone().getCode())
                
                .locationDetails(issue.getLocationDetails())
                .resolutionNotes(issue.getResolutionNotes())
                .customerSatisfactionRating(issue.getCustomerSatisfactionRating())
                
                .createdAt(issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : null)
                .updatedAt(issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : null)
                .build();
    }

    public IssueComment toIssueComment(IssueCommentRequest request) {
        CommentType commentType = CommentType.GENERAL;
        if (request.getCommentType() != null) {
            try {
                commentType = CommentType.valueOf(request.getCommentType().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Keep default comment type
            }
        }

        return IssueComment.builder()
                .comment(request.getComment())
                .commentType(commentType)
                .isInternal(request.getIsInternal() != null ? request.getIsInternal() : false)
                .attachmentUrl(request.getAttachmentUrl())
                .build();
    }

    public IssueCommentResponse toCommentResponse(IssueComment comment) {
        return IssueCommentResponse.builder()
                .id(comment.getCommentId())
                .issueId(comment.getIssue().getIssueId())
                .comment(comment.getComment())
                .commentType(comment.getCommentType().name())
                .isInternal(comment.getIsInternal())
                .attachmentUrl(comment.getAttachmentUrl())
                .createdByUserId(comment.getCreatedBy())
                .createdByUserName(comment.getCreatedByUser() != null ? 
                    comment.getCreatedByUser().getFirstname() + " " + comment.getCreatedByUser().getLastname() : null)
                .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null)
                .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null)
                .build();
    }
}