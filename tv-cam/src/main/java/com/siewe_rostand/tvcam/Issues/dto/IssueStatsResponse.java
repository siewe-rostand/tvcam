package com.siewe_rostand.tvcam.Issues.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Issue statistics response
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueStatsResponse {

    private Long totalIssues;
    private Long openIssues;
    private Long inProgressIssues;
    private Long resolvedIssues;
    private Long closedIssues;
    private Long cancelledIssues;
    private Long overdueIssues;
    
    // Priority breakdown
    private Long urgentIssues;
    private Long highPriorityIssues;
    private Long mediumPriorityIssues;
    private Long lowPriorityIssues;
    
    // Average resolution time in hours
    private Double averageResolutionTime;
    
    // Customer satisfaction
    private Double averageSatisfactionRating;
    private Long totalRatings;
}