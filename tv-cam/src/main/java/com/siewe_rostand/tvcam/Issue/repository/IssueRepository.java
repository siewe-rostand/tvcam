package com.siewe_rostand.tvcam.Issue.repository;

import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Issue.model.Issue;
import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueStatus;
import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueType;
import com.siewe_rostand.tvcam.Issue.model.enumeration.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Issue
 * 
 * @author rostand
 * @project tv-cam
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    Optional<Issue> findByReferenceNumber(String referenceNumber);

    List<Issue> findByCustomer(Customers customer);

    List<Issue> findByAssignedToUserId(Long userId);

    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByIssueType(IssueType issueType);

    List<Issue> findByPriority(Priority priority);

    @Query("SELECT i FROM Issue i WHERE i.customer.zone.zoneId IN :zoneIds")
    Page<Issue> findByCustomerZoneIds(@Param("zoneIds") List<Long> zoneIds, Pageable pageable);

    @Query("SELECT i FROM Issue i WHERE i.assignedTo.userId = :userId AND i.status IN :statuses")
    List<Issue> findByAssignedToAndStatuses(@Param("userId") Long userId,
            @Param("statuses") List<IssueStatus> statuses);

    @Query("SELECT i FROM Issue i WHERE i.dueDate < :date AND i.status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED')")
    List<Issue> findOverdueIssues(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.status = :status")
    Long countByStatus(@Param("status") IssueStatus status);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.assignedTo.userId = :userId AND i.status IN ('OPEN', 'IN_PROGRESS')")
    Long countActiveIssuesByAssignee(@Param("userId") Long userId);

    @Query("SELECT i FROM Issue i WHERE " +
            "(LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.referenceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:issueType IS NULL OR i.issueType = :issueType) AND " +
            "(:priority IS NULL OR i.priority = :priority)")
    Page<Issue> findWithFilters(@Param("searchTerm") String searchTerm,
            @Param("status") IssueStatus status,
            @Param("issueType") IssueType issueType,
            @Param("priority") Priority priority,
            Pageable pageable);
}
