package com.ledacosmeticos.api.Repository;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.TipoCategoria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Adicione a importação
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    // --- NOVOS MÉTODOS ---
    
    // Busca todos os produtos que estão marcados como ativos
    List<Produto> findByAtivoTrue();

    // Busca produtos ativos que pertencem a uma categoria
    List<Produto> findByCategoriaIdAndAtivoTrue(UUID categoriaId);
    
    // Busca produtos ativos pelo nome (para a sua barra de pesquisa)
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    List<Produto> findByCategoriaTipoAndAtivoTrue(TipoCategoria tipo);
}