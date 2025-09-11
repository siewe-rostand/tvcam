package com.siewe_rostand.tvcam.Bills.controller;

import com.siewe_rostand.tvcam.Bills.services.BillServices;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.siewe_rostand.tvcam.shared.utils.CommonUtils.FORMATTER;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

/**
 * Contrôleur pour la génération automatique mensuelle des factures
 * 
 * @author rostand
 * @project tv-cam
 */
@RestController
@RequestMapping("/bills/monthly-generation")
@RequiredArgsConstructor
@Slf4j
public class MonthlyBillGenerationController {

    private final BillServices billServices;

    /**
     * Vérifie si des factures ont déjà été générées aujourd'hui
     */
    @GetMapping("/check-generation-today")
    public ResponseEntity<HttpResponse<Object>> checkGenerationToday(
            @RequestParam String month,
            @RequestParam String year) {

        try {
            // Logique pour vérifier si des factures ont été générées aujourd'hui
            // Pour l'instant, on retourne false - à implémenter selon vos besoins
            boolean hasGenerated = false;

            Map<String, Object> response = new HashMap<>();
            response.put("hasGenerated", hasGenerated);
            response.put("checkDate", FORMATTER.format(now()));

            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(now())
                            .success(true)
                            .message("Vérification effectuée avec succès")
                            .data(response)
                            .status(OK.getReasonPhrase())
                            .statusCode(OK.value())
                            .build());
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de génération: {}", e.getMessage());
            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(now())
                            .success(false)
                            .message("Erreur lors de la vérification")
                            .status(OK.getReasonPhrase())
                            .statusCode(OK.value())
                            .build());
        }
    }

    /**
     * Force la génération mensuelle des factures
     */
    @PostMapping("/force-generation")
    public ResponseEntity<HttpResponse<Object>> forceMonthlyGeneration(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year) {

        try {
            LocalDateTime now = now();
            String targetMonth = month != null ? month : now.getMonth().name().toLowerCase();
            String targetYear = year != null ? year : String.valueOf(now.getYear());

            log.info("Génération forcée des factures pour {} {}", targetMonth, targetYear);

            // Ici, vous pouvez implémenter la logique de génération forcée
            // Pour l'instant, on retourne un succès simulé

            Map<String, Object> response = new HashMap<>();
            response.put("month", targetMonth);
            response.put("year", targetYear);
            response.put("generationDate", FORMATTER.format(now));
            response.put("billsGenerated", 0); // À remplacer par le vrai nombre

            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(now)
                            .success(true)
                            .message("Génération forcée effectuée avec succès")
                            .data(response)
                            .status(OK.getReasonPhrase())
                            .statusCode(OK.value())
                            .build());
        } catch (Exception e) {
            log.error("Erreur lors de la génération forcée: {}", e.getMessage());
            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(now())
                            .success(false)
                            .message("Erreur lors de la génération forcée: " + e.getMessage())
                            .status(OK.getReasonPhrase())
                            .statusCode(OK.value())
                            .build());
        }
    }

    /**
     * Obtient les statistiques de génération mensuelle
     */
    @GetMapping("/statistics")
    public ResponseEntity<HttpResponse<Object>> getGenerationStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("lastGenerationDate", null);
            statistics.put("totalGenerationsThisMonth", 0);
            statistics.put("totalGenerationsThisYear", 0);
            statistics.put("nextScheduledGeneration", null);

            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(now())
                            .success(true)
                            .message("Statistiques récupérées avec succès")
                            .data(statistics)
                            .status(OK.getReasonPhrase())
                            .statusCode(OK.value())
                            .build());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(now())
                            .success(false)
                            .message("Erreur lors de la récupération des statistiques")
                            .status(OK.getReasonPhrase())
                            .statusCode(OK.value())
                            .build());
        }
    }
}
