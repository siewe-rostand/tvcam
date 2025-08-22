package com.siewe_rostand.tvcam.Issues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Issue comment creation requests
 * 
 * @author rostand
 * @project tv-cam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueCommentRequest {

    @NotNull(message = "Issue ID is required")
    private Long issueId;

    @NotBlank(message = "Comment is required")
    private String comment;

    private String commentType;

    private Boolean isInternal = false;

    private String attachmentUrl;
}