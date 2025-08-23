package com.siewe_rostand.tvcam.Dashboard.service;

import java.util.Map;

/**
 * Interface du service Dashboard
 * 
 * @author rostand
 * @project tv-cam
 */
public interface DashboardService {

    Map<String, Object> getGeneralStatistics();

    Map<String, Object> getZoneStatistics();

    Map<String, Object> getPaymentStatistics();

    Map<String, Object> getIssueStatistics();

    Map<String, Object> getPerformanceMetrics();

    Map<String, Object> getUserStatistics(Long userId);

    Map<String, Object> getRecentActivities(Integer limit);

    Map<String, Object> getAlerts();
}
