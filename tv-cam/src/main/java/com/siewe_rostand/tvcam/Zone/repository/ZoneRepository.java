package com.siewe_rostand.tvcam.Zone.repository;

import com.siewe_rostand.tvcam.Zone.model.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Zone
 * 
 * @author rostand
 * @project tv-cam
 */
@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByCode(String code);

    List<Zone> findByIsActiveTrue();

    @Query("SELECT z FROM Zone z WHERE z.isActive = true AND " +
            "(LOWER(z.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(z.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Zone> findActiveZonesWithSearch(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT z FROM Zone z JOIN z.users u WHERE u.userId = :userId AND z.isActive = true")
    List<Zone> findZonesByUserId(@Param("userId") Long userId);

    boolean existsByCode(String code);

    // Méthodes pour la recherche avancée
    Page<Zone> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);

    Page<Zone> findByZoneIdIn(List<Long> zoneIds, Pageable pageable);

    List<Zone> findByZoneIdInAndIsActiveTrue(List<Long> zoneIds);
}
