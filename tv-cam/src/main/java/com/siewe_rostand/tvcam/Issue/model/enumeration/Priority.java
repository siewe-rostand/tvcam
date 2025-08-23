package com.siewe_rostand.tvcam.Issue.model.enumeration;

import lombok.Getter;

/**
 * Priorités pour les incidents/réclamations
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
public enum Priority {
    LOW("Faible"),
    MEDIUM("Moyenne"),
    HIGH("Élevée"),
    URGENT("Urgente");

    private final String description;

    Priority(String description) {
        this.description = description;
    }

}
