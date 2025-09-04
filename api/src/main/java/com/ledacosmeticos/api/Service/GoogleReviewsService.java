package com.ledacosmeticos.api.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleReviewsService {

    @Value("${google.places.api_key}")
    private String apiKey;

    @Value("${google.places.place_id}")
    private String placeId;

    // --- CORREÇÃO AQUI ---
    // Mudamos o tipo de retorno de String para Object
    public Object fetchReviews() {
         System.out.println(">>> GoogleReviewsService: Tentando usar a chave de API que termina em: " + (apiKey != null ? apiKey.substring(apiKey.length() - 6) : "CHAVE NULA"));
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(
            "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=name,rating,reviews&language=pt_BR&key=%s",
            placeId, apiKey
        );

        try {
            // Agora pedimos ao RestTemplate para interpretar a resposta como um objeto
            return restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar avaliações do Google: " + e.getMessage());
            // Retornamos null em caso de erro para o controller saber que falhou
            return null;
        }
    }
}