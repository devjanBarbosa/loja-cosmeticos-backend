package com.ledacosmeticos.api.Repository;

import com.ledacosmeticos.api.Model.Categoria;
import com.ledacosmeticos.api.Model.TipoCategoria; // 1. Importe o enum
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // 2. Importe a List
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    // --- MÉTODO ADICIONADO ---
    List<Categoria> findByTipo(TipoCategoria tipo); // 3. Adicione o método de busca
}