package com.ledacosmeticos.api.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto cadastrar(Produto produto){
      return produtoRepository.save(produto);
    }

    public Optional<Produto> buscarPorId(UUID id) {
        return produtoRepository.findById(id);
    }

    // Dentro de ProdutoService.java

public Produto atualizar(UUID id, Produto produtoComNovosDados) {
    // 1. Busca o produto que já existe no banco. Lançará um erro se não encontrar.
    Produto produtoExistente = produtoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

    // 2. Atualiza os campos do objeto existente com os novos dados.
    produtoExistente.setNome(produtoComNovosDados.getNome());
    produtoExistente.setDescricao(produtoComNovosDados.getDescricao());
    produtoExistente.setPreco(produtoComNovosDados.getPreco());
    produtoExistente.setUrlImagem(produtoComNovosDados.getUrlImagem());
    produtoExistente.setEstoque(produtoComNovosDados.getEstoque());
    // A categoria também pode ser atualizada
    produtoExistente.setCategoria(produtoComNovosDados.getCategoria());

  
    return produtoRepository.save(produtoExistente);
}
    public boolean deletar(UUID id) {
    // existsById é um pouco mais eficiente que findById para este caso
    if (produtoRepository.existsById(id)) {
        produtoRepository.deleteById(id);
        return true; // Sim, foi deletado
    }
    return false; // Não, o produto não foi encontrado
}
}