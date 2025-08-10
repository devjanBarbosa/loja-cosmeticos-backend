package com.ledacosmeticos.api.DTO;

import java.util.List;

// Este DTO agora reflete TODOS os campos que o seu formulário Angular envia.
public record PedidoRequestDTO(
    String nomeCliente,
    String whatsappCliente,
    String metodoEntrega,
    String metodoPagamento,
    String cep,
    String endereco,
    String numero,
    String complemento,
    String bairro,
    List<ItemDTO> itens
) {
    public record ItemDTO(
        String produtoId,
        int quantidade
    ) {}
}