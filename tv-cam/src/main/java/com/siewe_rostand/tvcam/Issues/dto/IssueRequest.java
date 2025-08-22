package com.siewe_rostand.tvcam.Issues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Issue creation and update requests
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueRequest {

    @NotBlank(message = "Issue title is required")
    private String title;

    @NotBlank(message = "Issue description is required")
    private String description;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Issue type ID is required")
    private Long issueTypeId;

    private Long assignedTechnicianId;

    private String priority;

    private String locationDetails;

    private LocalDateTime expectedResolutionDate;

    // For creating issues on behalf of customers
    private String customerTelephone;
}