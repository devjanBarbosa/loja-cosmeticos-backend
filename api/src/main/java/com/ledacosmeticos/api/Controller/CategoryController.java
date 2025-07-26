package com.ledacosmeticos.api.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ledacosmeticos.api.Model.Categoria;
import com.ledacosmeticos.api.Service.CategoryService; // Você precisará criar este serviço

@CrossOrigin
@RestController
@RequestMapping("/api/categorias")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<Categoria> listarTodas() {
        return categoryService.listarTodas();
    }
}