package com.siewe_rostand.tvcam.Zone.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

/**
 * Générateur de codes de zone uniques
 * 
 * @author rostand
 * @project tv-cam
 */
@Service
public class ZoneCodeGenerator {

    private static final String PREFIX = "ZN";
    private static final Random random = new Random();

    /**
     * Génère un code de zone unique au format ZN-YYYY-XXXX
     * Exemple: ZN-2025-A12B
     */
    public String generateZoneCode() {
        int currentYear = LocalDate.now().getYear();
        String randomSuffix = generateRandomAlphanumeric(4);

        return String.format("%s-%d-%s", PREFIX, currentYear, randomSuffix);
    }

    /**
     * Génère un code de zone basé sur le nom de la zone
     * Exemple: "Douala Centre" -> ZN-2025-DCTR
     */
    public String generateZoneCodeFromName(String zoneName) {
        int currentYear = LocalDate.now().getYear();
        String codeFromName = extractCodeFromName(zoneName);

        return String.format("%s-%d-%s", PREFIX, currentYear, codeFromName);
    }

    /**
     * Extrait un code de 4 caractères à partir du nom de la zone
     */
    private String extractCodeFromName(String zoneName) {
        if (zoneName == null || zoneName.trim().isEmpty()) {
            return generateRandomAlphanumeric(4);
        }

        // Supprimer les espaces et caractères spéciaux
        String cleanName = zoneName.toUpperCase()
                .replaceAll("[^A-Z0-9]", "");

        if (cleanName.length() >= 4) {
            // Prendre les 4 premiers caractères
            return cleanName.substring(0, 4);
        } else if (cleanName.length() >= 2) {
            // Compléter avec des caractères aléatoires
            String suffix = generateRandomAlphanumeric(4 - cleanName.length());
            return cleanName + suffix;
        } else {
            // Nom trop court, générer un code aléatoire
            return generateRandomAlphanumeric(4);
        }
    }

    /**
     * Génère une chaîne alphanumérique aléatoire
     */
    private String generateRandomAlphanumeric(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    /**
     * Valide le format d'un code de zone
     */
    public boolean isValidZoneCode(String code) {
        if (code == null) {
            return false;
        }

        // Format: ZN-YYYY-XXXX
        return code.matches("^ZN-\\d{4}-[A-Z0-9]{4}$");
    }

    /**
     * Génère un code de zone temporaire pour les tests
     */
    public String generateTestZoneCode() {
        return String.format("%s-TEST-%s", PREFIX, generateRandomAlphanumeric(4));
    }
}
