package com.siewe_rostand.tvcam.Issues.model;

import com.siewe_rostand.tvcam.Issues.model.enumeration.CommentType;
import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * IssueComment entity representing comments on issues
 * 
 * @author rostand
 * @project tv-cam
 */
@Entity
@Table(name = "issue_comments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"issue"})
public class IssueComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @NotBlank(message = "Comment is required")
    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", nullable = false)
    private CommentType commentType = CommentType.GENERAL;

    @Column(name = "is_internal", nullable = false)
    private Boolean isInternal = false;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    private Users createdByUser;
}