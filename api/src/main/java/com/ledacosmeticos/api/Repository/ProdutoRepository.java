package com.ledacosmeticos.api.Repository;

import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
// --- NOVA IMPORTAÇÃO ---
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; 
import java.util.List;
import java.util.UUID;

// --- ALTERAÇÃO AQUI ---
public interface ProdutoRepository extends JpaRepository<Produto, UUID>, JpaSpecificationExecutor<Produto> {

    // --- MÉTODOS EXISTENTES (sem alterações) ---
    List<Produto> findByAtivoTrue();
    List<Produto> findByCategoriaIdAndAtivoTrue(UUID categoriaId);
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    List<Produto> findByCategoriaTipoAndAtivoTrue(TipoCategoria tipo);

       List<Produto> findTop5ByEstoqueGreaterThanOrderByEstoqueAsc(int minEstoque);

       
}