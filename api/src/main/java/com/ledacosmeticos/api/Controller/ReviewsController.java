package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Service.GoogleReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

    @Autowired
    private GoogleReviewsService googleReviewsService;

    // --- CORREÇÃO AQUI ---
    // Mudamos o tipo de retorno de ResponseEntity<String> para ResponseEntity<Object>
    @GetMapping
    public ResponseEntity<Object> getReviews() {
        Object reviewsObject = googleReviewsService.fetchReviews();
        
        if (reviewsObject != null) {
            return ResponseEntity.ok(reviewsObject);
        } else {
            // Se o serviço falhar, retornamos um erro de servidor
            return ResponseEntity.internalServerError().build();
        }
    }
}