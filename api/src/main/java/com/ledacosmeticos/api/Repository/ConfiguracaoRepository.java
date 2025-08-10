package com.ledacosmeticos.api.Repository;

import com.ledacosmeticos.api.Model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, String> {
    // O JpaRepository já nos dá os métodos que precisamos
}