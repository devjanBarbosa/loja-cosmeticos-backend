package com.ledacosmeticos.api.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ledacosmeticos.api.Model.Produto;
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {}