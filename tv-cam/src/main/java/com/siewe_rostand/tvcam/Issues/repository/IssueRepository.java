package com.siewe_rostand.tvcam.Issues.repository;

import com.siewe_rostand.tvcam.Customers.Customers;
import com.siewe_rostand.tvcam.Issues.model.Issue;
import com.siewe_rostand.tvcam.Issues.model.IssueType;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssuePriority;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssueStatus;
import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.Zones.model.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Issue entity
 * 
 * @author rostand
 * @project tv-cam
 */
public interface IssueRepository extends JpaRepository<Issue, Long> {
    
    Optional<Issue> findByReference(String reference);
    
    List<Issue> findAllByCustomer(Customers customer);
    
    List<Issue> findAllByCustomerAndStatus(Customers customer, IssueStatus status);
    
    List<Issue> findAllByZone(Zone zone);
    
    List<Issue> findAllByZoneAndStatus(Zone zone, IssueStatus status);
    
    List<Issue> findAllByAssignedTechnician(Users technician);
    
    List<Issue> findAllByAssignedTechnicianAndStatus(Users technician, IssueStatus status);
    
    List<Issue> findAllByIssueType(IssueType issueType);
    
    List<Issue> findAllByPriority(IssuePriority priority);
    
    List<Issue> findAllByStatus(IssueStatus status);
    
    @Query("SELECT i FROM Issue i WHERE i.zone IN :zones")
    Page<Issue> findAllByZoneIn(@Param("zones") List<Zone> zones, Pageable pageable);
    
    @Query("SELECT i FROM Issue i WHERE i.zone IN :zones AND i.status = :status")
    Page<Issue> findAllByZoneInAndStatus(@Param("zones") List<Zone> zones, @Param("status") IssueStatus status, Pageable pageable);
    
    @Query("SELECT i FROM Issue i WHERE " +
           "i.zone IN :zones AND " +
           "(i.title LIKE %:searchTerm% OR i.description LIKE %:searchTerm% OR i.reference LIKE %:searchTerm% OR " +
           "i.customer.name LIKE %:searchTerm% OR i.issueType.name LIKE %:searchTerm%)")
    Page<Issue> findAllByZoneInWithSearch(@Param("zones") List<Zone> zones, 
                                         @Param("searchTerm") String searchTerm, 
                                         Pageable pageable);
    
    @Query("SELECT i FROM Issue i WHERE " +
           "(i.title LIKE %:searchTerm% OR i.description LIKE %:searchTerm% OR i.reference LIKE %:searchTerm% OR " +
           "i.customer.name LIKE %:searchTerm% OR i.issueType.name LIKE %:searchTerm%)")
    Page<Issue> findAllWithSearch(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.zone IN :zones AND i.status = :status")
    Long countByZoneInAndStatus(@Param("zones") List<Zone> zones, @Param("status") IssueStatus status);
    
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.assignedTechnician = :technician AND i.status = :status")
    Long countByAssignedTechnicianAndStatus(@Param("technician") Users technician, @Param("status") IssueStatus status);
    
    @Query("SELECT i FROM Issue i WHERE i.expectedResolutionDate < :currentDate AND i.status NOT IN :excludedStatuses")
    List<Issue> findOverdueIssues(@Param("currentDate") LocalDateTime currentDate, 
                                 @Param("excludedStatuses") List<IssueStatus> excludedStatuses);
    
    boolean existsByReference(String reference);
}