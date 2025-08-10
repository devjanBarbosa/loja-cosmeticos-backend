package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Service.ConfiguracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfiguracaoController {

    @Autowired
    private ConfiguracaoService configuracaoService;

    @GetMapping("/taxa-entrega")
    public ResponseEntity<Map<String, String>> getTaxaEntrega() {
        String taxa = configuracaoService.getTaxaEntrega();
        return ResponseEntity.ok(Map.of("taxaEntrega", taxa));
    }
}