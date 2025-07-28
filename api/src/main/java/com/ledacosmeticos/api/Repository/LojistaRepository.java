package com.ledacosmeticos.api.Repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ledacosmeticos.api.Model.Lojista;
public interface LojistaRepository extends JpaRepository<Lojista, UUID> {}