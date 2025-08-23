package com.siewe_rostand.tvcam.Issue.dto;

import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueStatus;
import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueType;
import com.siewe_rostand.tvcam.Issue.model.enumeration.Priority;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses Issue
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueResponse {

    private Long issueId;
    private String title;
    private String description;
    private IssueType issueType;
    private Priority priority;
    private IssueStatus status;
    private String referenceNumber;

    // Informations du client
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // Utilisateur assigné
    private Long assignedToId;
    private String assignedToName;

    // Dates
    private String createdAt;
    private String updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime dueDate;

    // Résolution
    private String resolution;
    private String notes;

    // Feedback client
    private Integer customerRating;
    private String customerFeedback;
}
