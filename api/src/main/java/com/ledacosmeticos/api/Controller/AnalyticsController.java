package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.DTO.DashboardDTO;
import com.ledacosmeticos.api.Service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // 1. Adicione esta importação
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardData(
            // 2. Adicione o @RequestParam para receber o filtro do frontend
            @RequestParam(name = "periodo", defaultValue = "mes") String periodo
    ) {
        // 3. Passe o período recebido para o serviço
        DashboardDTO dashboardData = analyticsService.getDashboardData(periodo);
        return ResponseEntity.ok(dashboardData);
    }
}