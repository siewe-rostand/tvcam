package com.siewe_rostand.tvcam.Zones.repository;

import com.siewe_rostand.tvcam.Zones.model.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Zone entity
 * 
 * @author rostand
 * @project tv-cam
 */
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    
    List<Zone> findAllByIsActiveTrue();
    
    Optional<Zone> findByCodeAndIsActiveTrue(String code);
    
    Optional<Zone> findByNameAndIsActiveTrue(String name);
    
    @Query("SELECT z FROM Zone z WHERE " +
           "(z.name LIKE %:searchTerm% OR z.code LIKE %:searchTerm% OR z.description LIKE %:searchTerm%) " +
           "AND (:includeInactive = true OR z.isActive = true)")
    Page<Zone> findZonesWithSearch(@Param("searchTerm") String searchTerm, 
                                  @Param("includeInactive") boolean includeInactive, 
                                  Pageable pageable);
    
    boolean existsByCodeAndIsActiveTrue(String code);
    
    boolean existsByNameAndIsActiveTrue(String name);
}