package com.ledacosmeticos.api.DTO;

public record ProdutoEstoqueBaixoDTO(
    String id,
    String nome,
    int estoque
) {}