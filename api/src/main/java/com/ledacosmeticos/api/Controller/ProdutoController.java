package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.TipoCategoria;
import com.ledacosmeticos.api.Service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;

    // --- MÉTODO UNIFICADO PARA LISTAR E FILTRAR PRODUTOS ---
     @GetMapping
    public ResponseEntity<List<Produto>> listarTodosOsProdutos(
            @RequestParam(name = "categoria", required = false) UUID categoriaId,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "tipo", required = false) TipoCategoria tipo) { // <-- Adicione este parâmetro
        

        try {
            List<Produto> produtos = produtoService.listarTodos(categoriaId, sortBy, tipo);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrarProduto(@RequestBody Produto produto) {
        Produto novoProduto = produtoService.cadastrar(produto);
        return ResponseEntity.status(201).body(novoProduto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable UUID id) {
        Optional<Produto> produtoOptional = produtoService.buscarPorId(id);
        return produtoOptional
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> pesquisarProdutos(@RequestParam("q") String termoDePesquisa) {
        List<Produto> produtosEncontrados = produtoService.pesquisarPorNome(termoDePesquisa);
        return ResponseEntity.ok(produtosEncontrados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable UUID id, @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable UUID id) {
        try {
            produtoService.inativar(id); // Chama o novo método de inativação
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}