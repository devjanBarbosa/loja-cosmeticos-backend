package com.ledacosmeticos.api.Repository;

import com.ledacosmeticos.api.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    // Este método vai permitir-nos encontrar um cliente pelo seu número de WhatsApp,
    // que estamos a usar como o seu identificador único.
    Optional<Cliente> findByWhatsapp(String whatsapp);
}