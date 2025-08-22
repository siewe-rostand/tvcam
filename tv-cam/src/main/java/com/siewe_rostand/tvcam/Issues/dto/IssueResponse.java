package com.siewe_rostand.tvcam.Issues.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Issue response data
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueResponse {

    private Long id;
    private String reference;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime reportedDate;
    private LocalDateTime expectedResolutionDate;
    private LocalDateTime actualResolutionDate;
    
    // Customer information
    private Long customerId;
    private String customerName;
    private String customerTelephone;
    private String customerAddress;
    
    // Issue type information
    private Long issueTypeId;
    private String issueTypeName;
    private String issueTypeCode;
    
    // Technician information
    private Long assignedTechnicianId;
    private String assignedTechnicianName;
    private String assignedTechnicianTelephone;
    
    // Zone information
    private Long zoneId;
    private String zoneName;
    private String zoneCode;
    
    private String locationDetails;
    private String resolutionNotes;
    private Integer customerSatisfactionRating;
    
    // Additional info
    private Long commentCount;
    private String createdAt;
    private String updatedAt;
    private String createdByName;
}