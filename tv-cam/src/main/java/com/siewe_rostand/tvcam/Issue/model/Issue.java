package com.siewe_rostand.tvcam.Issue.model;

import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueStatus;
import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueType;
import com.siewe_rostand.tvcam.Issue.model.enumeration.Priority;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

/**
 * Entité Issue pour la gestion des réclamations et incidents
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
@ToString
public class Issue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long issueId;

    @NotBlank(message = "Le titre de la réclamation est obligatoire")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Le type d'incident est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "issue_type", nullable = false)
    private IssueType issueType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @ColumnDefault("'MEDIUM'")
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @ColumnDefault("'OPEN'")
    private IssueStatus status = IssueStatus.OPEN;

    @Column(name = "reference_number", unique = true)
    private String referenceNumber;

    // Client qui a signalé le problème
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    // Utilisateur assigné pour traiter le problème
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private Users assignedTo;

    // Date de résolution
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // Date limite de résolution
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    // Solution apportée
    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    // Notes supplémentaires
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Évaluation du client (1-5 étoiles)
    @Column(name = "customer_rating")
    private Integer customerRating;

    // Commentaire du client après résolution
    @Column(name = "customer_feedback")
    private String customerFeedback;

    @PrePersist
    protected void onPrePersist() {
        if (this.referenceNumber == null) {
            this.referenceNumber = generateReferenceNumber();
        }
    }

    private String generateReferenceNumber() {
        return "ISS-" + System.currentTimeMillis();
    }
}
