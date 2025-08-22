package com.siewe_rostand.tvcam.Issues.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Issue status update requests
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueStatusUpdateRequest {

    @NotNull(message = "Issue ID is required")
    private Long issueId;

    @NotNull(message = "Status is required")
    private String status;

    private String resolutionNotes;

    private LocalDateTime actualResolutionDate;

    @Min(value = 1, message = "Customer satisfaction rating must be between 1 and 5")
    @Max(value = 5, message = "Customer satisfaction rating must be between 1 and 5")
    private Integer customerSatisfactionRating;

    private String statusComment;
}