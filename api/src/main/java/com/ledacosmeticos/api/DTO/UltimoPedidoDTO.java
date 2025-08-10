package com.ledacosmeticos.api.DTO;

public record UltimoPedidoDTO(
    String id,
    String nomeCliente,
    double valorTotal,
    String status
) {}