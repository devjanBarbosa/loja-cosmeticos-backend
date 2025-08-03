package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Model.Categoria;
import com.ledacosmeticos.api.Model.TipoCategoria; // 1. Importe
import com.ledacosmeticos.api.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 2. Garanta que esta importação está completa

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // --- MÉTODO ATUALIZADO ---
    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas(
            @RequestParam(name = "tipo", required = false) TipoCategoria tipo) { // 3. Adicione o parâmetro

        System.out.println("=== ENDPOINT /api/categorias CHAMADO ===");
        try {
            // Agora passamos o filtro para o serviço
            List<Categoria> categorias = categoryService.listarTodas(tipo);
            System.out.println("Categorias encontradas: " + categorias.size());
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

     @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody Categoria categoria) {
        Categoria novaCategoria = categoryService.criar(categoria);
        return ResponseEntity.status(201).body(novaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable UUID id, @RequestBody Categoria categoria) {
        try {
            Categoria categoriaAtualizada = categoryService.atualizar(id, categoria);
            return ResponseEntity.ok(categoriaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable UUID id) {
        try {
            categoryService.deletar(id);
            return ResponseEntity.noContent().build(); // Sucesso, sem conteúdo
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}