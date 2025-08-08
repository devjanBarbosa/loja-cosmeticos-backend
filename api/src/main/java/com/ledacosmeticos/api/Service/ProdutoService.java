package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.TipoCategoria;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification; // <-- NOVA IMPORTAÇÃO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // --- NOVA INJEÇÃO DE DEPENDÊNCIA ---
    @Autowired
    private ProdutoSpecification produtoSpecification;

    // ... (os seus outros métodos como cadastrar, atualizar, etc., não mudam)

    // --- MÉTODO ATUALIZADO PARA USAR SPECIFICATIONS ---
    public List<Produto> listarParaAdmin(String nome, UUID categoriaId, Boolean ativo) {
        // 1. Pedimos ao nosso construtor para criar a query com os filtros recebidos
        Specification<Produto> spec = produtoSpecification.findWithFilters(nome, categoriaId, ativo);
        
        // 2. O repositório agora sabe como executar esta query programática
        return produtoRepository.findAll(spec);
    }
    
    // ... (resto do seu serviço)
    
    // ATUALIZADO: Agora só lista produtos ativos
     public List<Produto> listarTodos(UUID categoriaId, String sortBy, TipoCategoria tipo) {
        if (categoriaId != null) {
            return produtoRepository.findByCategoriaIdAndAtivoTrue(categoriaId);
        }
        if (tipo != null) {
            return produtoRepository.findByCategoriaTipoAndAtivoTrue(tipo);
        }
        return produtoRepository.findByAtivoTrue();
    }

    public Produto cadastrar(Produto produto){
        produto.setAtivo(true);
        return produtoRepository.save(produto);
    }
    
    public List<Produto> pesquisarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public Optional<Produto> buscarPorId(UUID id) {
        return produtoRepository.findById(id);
    }

    public Produto atualizar(UUID id, Produto produtoComNovosDados) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

        produtoExistente.setNome(produtoComNovosDados.getNome());
        produtoExistente.setDescricao(produtoComNovosDados.getDescricao());
        produtoExistente.setPreco(produtoComNovosDados.getPreco());
        produtoExistente.setUrlImagem(produtoComNovosDados.getUrlImagem());
        produtoExistente.setEstoque(produtoComNovosDados.getEstoque());
        produtoExistente.setCategoria(produtoComNovosDados.getCategoria());

        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void inativar(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado para inativar!"));
        
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}