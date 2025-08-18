package com.ledacosmeticos.api.DTO;

// Usamos um 'record' para um DTO simples e imutável.
// Ele garante que o JSON recebido do frontend terá a chave "status".
public record StatusUpdateRequestDTO(String status) {
}
