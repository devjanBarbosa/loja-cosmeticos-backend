package com.ledacosmeticos.api.DTO;

// Este record representa os dados de um único item vindo do frontend.
public record ItemDTO(
    String produtoId,
    int quantidade
) {}