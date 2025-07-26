package com.ledacosmeticos.api.Repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ledacosmeticos.api.Model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {}