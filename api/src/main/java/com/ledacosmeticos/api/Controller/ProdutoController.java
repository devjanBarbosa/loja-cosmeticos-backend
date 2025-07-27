package com.ledacosmeticos.api.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Service.ProdutoService;


@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodosOsProdutos() {
        System.out.println("=== ENDPOINT /api/produtos CHAMADO ===");
        try {
            List<Produto> produtos = produtoService.listarTodos();
            System.out.println("Produtos encontrados: " + produtos.size());
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrarProduto(@RequestBody Produto produto) {
        System.out.println("=== CADASTRANDO PRODUTO ===");
        try {
            Produto novoProduto = produtoService.cadastrar(produto);
            return ResponseEntity.status(201).body(novoProduto);
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar produto: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable UUID id) {
        System.out.println("=== BUSCANDO PRODUTO POR ID: " + id + " ===");
        try {
            Optional<Produto> produtoOptional = produtoService.buscarPorId(id);
            return produtoOptional
                .map(produto -> ResponseEntity.ok(produto))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Erro ao buscar produto: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable UUID id, @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable UUID id) {
        try {
            if (produtoService.deletar(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Erro ao deletar produto: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}