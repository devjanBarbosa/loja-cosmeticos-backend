package com.ledacosmeticos.api.DTO;

public record ItemPedidoResponseDTO(
    String nomeProduto,
    Integer quantidade,
    Double precoUnitario
) {}