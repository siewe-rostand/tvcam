package com.siewe_rostand.tvcam.Issue.model.enumeration;

import lombok.Getter;

/**
 * Types d'incidents/réclamations possibles
 * 
 * @author rostand
 * @project tv-cam
 */
@Getter
public enum IssueType {
    TECHNICAL_ISSUE("Problème technique"),
    POWER_OUTAGE("Coupure d'électricité"),
    SIGNAL_LOSS("Perte de signal"),
    EQUIPMENT_FAILURE("Panne d'équipement"),
    INSTALLATION_ISSUE("Problème d'installation"),
    BILLING_DISPUTE("Contestation de facture"),
    SERVICE_REQUEST("Demande de service"),
    POLE_DOWN("Poteau tombé"),
    NO_IMAGE("Pas d'image"),
    POOR_SIGNAL_QUALITY("Mauvaise qualité de signal"),
    CABLE_DAMAGE("Câble endommagé"),
    CUSTOMER_COMPLAINT("Réclamation client"),
    OTHER("Autre");

    private final String description;

    IssueType(String description) {
        this.description = description;
    }

}
