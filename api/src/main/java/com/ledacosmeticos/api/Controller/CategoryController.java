package com.ledacosmeticos.api.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ledacosmeticos.api.Model.Categoria;
import com.ledacosmeticos.api.Service.CategoryService;


@RestController
@RequestMapping("/api/categorias")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas() {
        System.out.println("=== ENDPOINT /api/categorias CHAMADO ===");
        try {
            List<Categoria> categorias = categoryService.listarTodas();
            System.out.println("Categorias encontradas: " + categorias.size());
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}