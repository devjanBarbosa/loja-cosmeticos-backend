package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.DTO.DashboardDTO;
import com.ledacosmeticos.api.Service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardData() {
        DashboardDTO dashboardData = analyticsService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }
}