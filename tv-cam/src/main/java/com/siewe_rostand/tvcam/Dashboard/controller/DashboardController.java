package com.siewe_rostand.tvcam.Dashboard.controller;

import com.siewe_rostand.tvcam.Dashboard.service.DashboardService;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

/**
 * Contrôleur pour le tableau de bord et les statistiques
 *
 * @author rostand
 * @project tv-cam
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API pour le tableau de bord et les statistiques")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Récupérer les statistiques générales")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF_CABLEUR') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> getGeneralStatistics() {
        Map<String, Object> stats = dashboardService.getGeneralStatistics();

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Statistiques récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(stats)
                .build());
    }

    @Operation(summary = "Récupérer les statistiques par zone")
    @GetMapping("/stats/zones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF_CABLEUR') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> getZoneStatistics() {
        Map<String, Object> stats = dashboardService.getZoneStatistics();

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Statistiques par zone récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(stats)
                .build());
    }

    @Operation(summary = "Récupérer les statistiques des paiements")
    @GetMapping("/stats/payments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECOUVREUR') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> getPaymentStatistics() {
        Map<String, Object> stats = dashboardService.getPaymentStatistics();

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Statistiques des paiements récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(stats)
                .build());
    }

    @Operation(summary = "Récupérer les statistiques des réclamations")
    @GetMapping("/stats/issues")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIEN') or hasRole('CHEF_CABLEUR')")
    public ResponseEntity<HttpResponse> getIssueStatistics() {
        Map<String, Object> stats = dashboardService.getIssueStatistics();

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Statistiques des réclamations récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(stats)
                .build());
    }

    @Operation(summary = "Récupérer les métriques de performance")
    @GetMapping("/performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<HttpResponse> getPerformanceMetrics() {
        Map<String, Object> metrics = dashboardService.getPerformanceMetrics();

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Métriques de performance récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(metrics)
                .build());
    }

    @Operation(summary = "Récupérer les statistiques pour un utilisateur spécifique")
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<HttpResponse> getUserStatistics(@PathVariable Long userId) {
        Map<String, Object> stats = dashboardService.getUserStatistics(userId);

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Statistiques utilisateur récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(stats)
                .build());
    }

    @Operation(summary = "Récupérer le résumé des activités récentes")
    @GetMapping("/recent-activities")
    public ResponseEntity<HttpResponse> getRecentActivities(@RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> activities = dashboardService.getRecentActivities(limit);

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Activités récentes récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(activities)
                .build());
    }

    @Operation(summary = "Récupérer les alertes et notifications")
    @GetMapping("/alerts")
    public ResponseEntity<HttpResponse> getAlerts() {
        Map<String, Object> alerts = dashboardService.getAlerts();

        return ResponseEntity.ok(HttpResponse.builder()
                .timestamp(now())
                .message("Alertes récupérées avec succès")
                .status(OK)
                .statusCode(OK.value())
                .data(alerts)
                .build());
    }
}
