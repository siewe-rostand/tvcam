package com.siewe_rostand.tvcam.Issues.repository;

import com.siewe_rostand.tvcam.Issues.model.Issue;
import com.siewe_rostand.tvcam.Issues.model.IssueComment;
import com.siewe_rostand.tvcam.Issues.model.enumeration.CommentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for IssueComment entity
 * 
 * @author rostand
 * @project tv-cam
 */
public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {
    
    List<IssueComment> findAllByIssueOrderByCreatedAtDesc(Issue issue);
    
    List<IssueComment> findAllByIssueAndIsInternalFalseOrderByCreatedAtDesc(Issue issue);
    
    List<IssueComment> findAllByIssueAndCommentTypeOrderByCreatedAtDesc(Issue issue, CommentType commentType);
    
    @Query("SELECT ic FROM IssueComment ic WHERE ic.issue = :issue AND " +
           "(:includeInternal = true OR ic.isInternal = false) " +
           "ORDER BY ic.createdAt DESC")
    Page<IssueComment> findCommentsByIssue(@Param("issue") Issue issue, 
                                          @Param("includeInternal") boolean includeInternal, 
                                          Pageable pageable);
    
    @Query("SELECT COUNT(ic) FROM IssueComment ic WHERE ic.issue = :issue AND ic.isInternal = false")
    Long countPublicCommentsByIssue(@Param("issue") Issue issue);
}