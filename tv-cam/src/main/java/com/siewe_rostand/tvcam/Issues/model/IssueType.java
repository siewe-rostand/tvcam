package com.siewe_rostand.tvcam.Issues.model;

import com.siewe_rostand.tvcam.Issues.model.enumeration.IssuePriority;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * IssueType entity representing different types of issues/complaints
 * 
 * @author rostand
 * @project tv-cam
 */
@Entity
@Table(name = "issue_types")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IssueType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long issueTypeId;

    @NotBlank(message = "Issue type name is required")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Issue type code is required")
    @Size(max = 20, message = "Issue type code must not exceed 20 characters")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    private IssuePriority priorityLevel = IssuePriority.MEDIUM;

    @Column(name = "estimated_resolution_time")
    private Integer estimatedResolutionTime; // in hours

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}