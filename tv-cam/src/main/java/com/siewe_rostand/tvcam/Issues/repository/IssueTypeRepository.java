package com.siewe_rostand.tvcam.Issues.repository;

import com.siewe_rostand.tvcam.Issues.model.IssueType;
import com.siewe_rostand.tvcam.Issues.model.enumeration.IssuePriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for IssueType entity
 * 
 * @author rostand
 * @project tv-cam
 */
public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {
    
    List<IssueType> findAllByIsActiveTrueOrderByName();
    
    Optional<IssueType> findByCodeAndIsActiveTrue(String code);
    
    Optional<IssueType> findByNameAndIsActiveTrue(String name);
    
    List<IssueType> findAllByPriorityLevelAndIsActiveTrue(IssuePriority priorityLevel);
    
    @Query("SELECT it FROM IssueType it WHERE " +
           "(it.name LIKE %:searchTerm% OR it.code LIKE %:searchTerm% OR it.description LIKE %:searchTerm%) " +
           "AND (:includeInactive = true OR it.isActive = true)")
    Page<IssueType> findIssueTypesWithSearch(@Param("searchTerm") String searchTerm, 
                                            @Param("includeInactive") boolean includeInactive, 
                                            Pageable pageable);
    
    boolean existsByCodeAndIsActiveTrue(String code);
    
    boolean existsByNameAndIsActiveTrue(String name);
}