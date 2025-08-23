package com.siewe_rostand.tvcam.Issue.model.enumeration;

import lombok.Getter;

/**
 * Statuts possibles pour les incidents/réclamations
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
public enum IssueStatus {
    OPEN("Ouvert"),
    IN_PROGRESS("En cours"),
    PENDING("En attente"),
    RESOLVED("Résolu"),
    CLOSED("Fermé"),
    CANCELLED("Annulé");

    private final String description;

    IssueStatus(String description) {
        this.description = description;
    }

}
