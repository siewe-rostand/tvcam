package com.siewe_rostand.tvcam.Issue.dto;

import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueType;
import com.siewe_rostand.tvcam.Issue.model.enumeration.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO pour les requêtes Issue
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueRequest {

    @NotBlank(message = "Le titre de la réclamation est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le type d'incident est obligatoire")
    private IssueType issueType;

    private Priority priority;

    @NotNull(message = "L'ID du client est obligatoire")
    private Long customerId;

    private Long assignedToId;

    private LocalDateTime dueDate;

    private String notes;
}
