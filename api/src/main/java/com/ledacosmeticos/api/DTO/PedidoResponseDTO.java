package com.ledacosmeticos.api.DTO;

import java.time.LocalDateTime;
import java.util.List;
import com.ledacosmeticos.api.Model.StatusPedido;

public record PedidoResponseDTO(
    String id,
    LocalDateTime dataDoPedido,
    String nomeCliente,
    StatusPedido status,
    Double valorTotal,
    List<ItemPedidoResponseDTO> itens
) {}