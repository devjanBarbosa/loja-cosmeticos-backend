package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.TipoCategoria;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // ATUALIZADO: Agora só lista produtos ativos
     public List<Produto> listarTodos(UUID categoriaId, String sortBy, TipoCategoria tipo) {
        // ... (sua lógica de ordenação continua igual) ...

        // --- LÓGICA DE FILTRO ATUALIZADA ---
        if (categoriaId != null) {
            return produtoRepository.findByCategoriaIdAndAtivoTrue(categoriaId);
        }
        if (tipo != null) {
            // Se um tipo for fornecido, filtra por ele
            return produtoRepository.findByCategoriaTipoAndAtivoTrue(tipo);
        }
        
        // Se nenhum filtro for aplicado, retorna todos os produtos ativos
        return produtoRepository.findByAtivoTrue();
    }

    public Produto cadastrar(Produto produto){
        produto.setAtivo(true); // Garante que o produto seja criado como ativo
        return produtoRepository.save(produto);
    }
    
    // ATUALIZADO: A pesquisa também só retorna produtos ativos
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

    // RENOMEADO E ATUALIZADO: De deletar para inativar
    @Transactional
    public void inativar(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado para inativar!"));
        
        produto.setAtivo(false); // Apenas marca como inativo
        produtoRepository.save(produto);
    }
}