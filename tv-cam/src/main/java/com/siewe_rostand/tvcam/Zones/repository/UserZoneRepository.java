package com.siewe_rostand.tvcam.Zones.repository;

import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.Zones.model.UserZone;
import com.siewe_rostand.tvcam.Zones.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserZone entity
 * 
 * @author rostand
 * @project tv-cam
 */
public interface UserZoneRepository extends JpaRepository<UserZone, Long> {
    
    List<UserZone> findAllByUserAndIsActiveTrue(Users user);
    
    List<UserZone> findAllByZoneAndIsActiveTrue(Zone zone);
    
    Optional<UserZone> findByUserAndZoneAndIsActiveTrue(Users user, Zone zone);
    
    Optional<UserZone> findByUserAndIsPrimaryZoneTrueAndIsActiveTrue(Users user);
    
    @Query("SELECT uz FROM UserZone uz WHERE uz.user = :user AND uz.isActive = true")
    List<UserZone> findActiveZonesByUser(@Param("user") Users user);
    
    @Query("SELECT uz.zone FROM UserZone uz WHERE uz.user = :user AND uz.isActive = true")
    List<Zone> findZonesByUser(@Param("user") Users user);
    
    @Query("SELECT uz.user FROM UserZone uz WHERE uz.zone = :zone AND uz.isActive = true")
    List<Users> findUsersByZone(@Param("zone") Zone zone);
    
    @Modifying
    @Query("UPDATE UserZone uz SET uz.isPrimaryZone = false WHERE uz.user = :user AND uz.isActive = true")
    void resetPrimaryZoneForUser(@Param("user") Users user);
    
    boolean existsByUserAndZoneAndIsActiveTrue(Users user, Zone zone);
}