package com.siewe_rostand.tvcam.Roles.enumeration;

import lombok.Getter;

/**
 * Énumération des rôles métier pour les câblo-distributeurs
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
public enum UserRole {
    ADMIN("Administrateur"),
    CHEF_CABLEUR("Chef Câbleur"),
    TECHNICIEN("Technicien"),
    RECOUVREUR("Recouvreur"),
    MANAGER("Manager");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

}
