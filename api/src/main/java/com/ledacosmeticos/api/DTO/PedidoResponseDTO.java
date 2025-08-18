package com.ledacosmeticos.api.DTO;

import java.time.LocalDateTime;
import java.util.List;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Model.TipoEntrega;


public record PedidoResponseDTO(
    String id,
    LocalDateTime dataCriacao,      // CORRIGIDO: Nome alinhado com o frontend
    String nomeCliente,
    String whatsappCliente,
    StatusPedido status,
    Double subtotal,                // ADICIONADO
    Double taxaEntrega,             // ADICIONADO
    Double valorTotal,
    List<ItemPedidoResponseDTO> itens,
    TipoEntrega metodoEntrega,      // CORRIGIDO: Nome alinhado com o frontend
    String metodoPagamento,
    String cep,
    String endereco,
    String numero,
    String complemento,
    String bairro,
    String pixCopiaECola,
    String pixQrCodeBase64
) {}
