package com.siewe_rostand.tvcam.Issues.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Issue comment response data
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueCommentResponse {

    private Long id;
    private Long issueId;
    private String comment;
    private String commentType;
    private Boolean isInternal;
    private String attachmentUrl;
    
    // User information
    private Long createdByUserId;
    private String createdByUserName;
    private String createdByUserRole;
    
    private String createdAt;
    private String updatedAt;
}