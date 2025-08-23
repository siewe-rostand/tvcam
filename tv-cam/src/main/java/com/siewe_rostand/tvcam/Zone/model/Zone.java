package com.siewe_rostand.tvcam.Zone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Users.models.Users;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

/**
 * Entité Zone pour la gestion géographique des câblo-distributeurs
 *
 * @author rostand
 * @project tv-cam
 */
@Entity
@Table(name = "zones")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Zone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long zoneId;

    @NotBlank(message = "Le nom de la zone est obligatoire")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotBlank(message = "Le code de la zone est obligatoire")
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Une zone peut contenir plusieurs clients
    @JsonIgnore
    @OneToMany(mappedBy = "zone", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Customers> customers;

    // Une zone peut avoir plusieurs utilisateurs (câbleurs, techniciens, etc.)
    @JsonIgnore
    @ManyToMany(mappedBy = "zones", fetch = FetchType.LAZY)
    private Set<Users> users;

    @Column(name = "address")
    private String address;
}
