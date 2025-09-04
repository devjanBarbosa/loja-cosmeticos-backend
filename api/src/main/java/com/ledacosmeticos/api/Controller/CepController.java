package com.ledacosmeticos.api.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/cep")
public class CepController {

    @GetMapping("/{cep}")
    public ResponseEntity<String> buscarEndereco(@PathVariable String cep) {
        // Remove qualquer caractere não numérico para segurança
        String cepSanitizado = cep.replaceAll("[^0-9]", "");
        
        String viaCepUrl = "https://viacep.com.br/ws/" + cepSanitizado + "/json/";
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // Faz a chamada do servidor para a API do ViaCEP
            ResponseEntity<String> response = restTemplate.getForEntity(viaCepUrl, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Em caso de erro (CEP não encontrado, etc.), retorna uma resposta de erro padrão
            return ResponseEntity.status(404).body("{\"erro\": true}");
        }
    }
}
