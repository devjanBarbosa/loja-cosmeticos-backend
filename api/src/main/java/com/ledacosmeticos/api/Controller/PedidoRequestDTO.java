package com.ledacosmeticos.api.Controller;

import java.util.List;

// Este DTO representa os dados que o frontend envia para criar um pedido.
public record PedidoRequestDTO(
    String nomeCliente,
    String whatsappCliente,
    List<ItemDTO> itens
) {
    public record ItemDTO(
        String produtoId,
        int quantidade
    ) {}
}