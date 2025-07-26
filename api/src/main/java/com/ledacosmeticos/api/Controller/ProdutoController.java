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
import org.springframework.web.bind.annotation.RestController;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Service.ProdutoService;


@CrossOrigin
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<Produto> listarTodosOsProdutos() {
        return produtoService.listarTodos();
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
        .map(produtoEncontrado -> ResponseEntity.ok(produtoEncontrado)) // Se o Optional tiver um produto, executa isso
        .orElseGet(() -> ResponseEntity.notFound().build()); // Se o Optional estiver vazio, executa isso
}

// Dentro de ProdutoController.java

@PutMapping("/{id}")
public ResponseEntity<Produto> atualizarProduto(@PathVariable UUID id, @RequestBody Produto produto) {
    // Tenta atualizar. Se o produto não for encontrado, o serviço lançará um erro.
    // Podemos melhorar o tratamento de erro depois, mas por enquanto isso funciona.
    try {
        Produto produtoAtualizado = produtoService.atualizar(id, produto);
        return ResponseEntity.ok(produtoAtualizado);
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deletarProduto(@PathVariable UUID id) {
    if (produtoService.deletar(id)) {
        return ResponseEntity.noContent().build(); // 204
    } else {
        return ResponseEntity.notFound().build(); // 404
    }
}
// Dentro de ProdutoController.java


}                                                                                                                               