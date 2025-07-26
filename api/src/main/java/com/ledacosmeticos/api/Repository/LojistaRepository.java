package com.ledacosmeticos.api.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.ledacosmeticos.api.Model.Lojista;


public interface LojistaRepository extends JpaRepository<Lojista, UUID> {
    // O Spring Security espera que o método de busca retorne um UserDetails
    UserDetails findByEmail(String email);
}