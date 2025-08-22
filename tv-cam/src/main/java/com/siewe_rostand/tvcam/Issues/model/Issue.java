package com.siewe_rostand.tvcam.Issues.model;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssuePriority;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssueStatus;
import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.Zones.model.Zone;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Issue entity representing customer complaints and issues
 * 
 * @author rostand
 * @project tv-cam
 */
@Entity
@Table(name = "issues")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"comments"})
public class Issue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long issueId;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotBlank(message = "Issue title is required")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Issue description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private IssuePriority priority = IssuePriority.MEDIUM;

    @Column(name = "reported_date", nullable = false)
    private LocalDateTime reportedDate;

    @Column(name = "expected_resolution_date")
    private LocalDateTime expectedResolutionDate;

    @Column(name = "actual_resolution_date")
    private LocalDateTime actualResolutionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_type_id", nullable = false)
    private IssueType issueType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician_id")
    private Users assignedTechnician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "location_details", columnDefinition = "TEXT")
    private String locationDetails;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Min(value = 1, message = "Customer satisfaction rating must be between 1 and 5")
    @Max(value = 5, message = "Customer satisfaction rating must be between 1 and 5")
    @Column(name = "customer_satisfaction_rating")
    private Integer customerSatisfactionRating;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IssueComment> comments;
}