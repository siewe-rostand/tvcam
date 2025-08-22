package com.siewe_rostand.tvcam.Zones.model;

import com.siewe_rostand.tvcam.Users.Users;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * UserZone entity representing the many-to-many relationship between Users and Zones
 * Allows a user to work in multiple zones and tracks primary zone assignment
 * 
 * @author rostand
 * @project tv-cam
 */
@Entity
@Table(name = "user_zones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "zone_id"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserZone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "is_primary_zone", nullable = false)
    private Boolean isPrimaryZone = false;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}