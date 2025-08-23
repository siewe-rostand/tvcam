package com.siewe_rostand.tvcam.Customers.repository;

import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Zone.model.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomersRepository extends JpaRepository<Customers, Long> {

    Optional<Customers> findByCustomerId(Long id);

    Boolean existsByTelephone(String telephone);

    Boolean existsByCustomerId(Long customerId);

    @Query("SELECT cus FROM Customers cus WHERE (cus.name LIKE ?1 OR ?1 IS NULL)")
    Page<Customers> findAll(String name, Pageable pageable);

    Page<Customers> findAll(Pageable pageable);

    @Query("SELECT cus FROM Customers cus WHERE cus.name LIKE ?1")
    List<Customers> findByKeyword(String keyword);

    @Query("SELECT cus FROM Customers cus WHERE cus.name LIKE ?1")
    Page<Customers> findByKeyword(String keyword, Pageable pageable);

    Page<Customers> findAllByIsActive(boolean isActive, Pageable pageable);

    // Nouvelles méthodes pour la gestion par zones
    @Query("SELECT c FROM Customers c WHERE c.zone.zoneId IN :zoneIds")
    Page<Customers> findByZoneIds(@Param("zoneIds") List<Long> zoneIds, Pageable pageable);

    @Query("SELECT c FROM Customers c WHERE c.zone.zoneId IN :zoneIds AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.telephone) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Customers> findByZoneIdsAndSearchTerm(@Param("zoneIds") List<Long> zoneIds,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    List<Customers> findByZoneZoneId(Long zoneId);

    @Query("SELECT COUNT(c) FROM Customers c WHERE c.zone.zoneId = :zoneId")
    Long countByZoneId(@Param("zoneId") Long zoneId);

    // Méthode pour ZoneService
    List<Customers> findByZone(Zone zone);
}
