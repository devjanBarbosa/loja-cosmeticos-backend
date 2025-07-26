package com.ledacosmeticos.api.Repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ledacosmeticos.api.Model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {}