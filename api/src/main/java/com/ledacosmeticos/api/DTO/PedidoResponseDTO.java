package com.ledacosmeticos.api.DTO;

import java.time.LocalDateTime;
import java.util.List;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Model.TipoEntrega;

// Adicione todos os campos que o frontend precisa de ver
public record PedidoResponseDTO(
    String id,
    LocalDateTime dataDoPedido,
    String nomeCliente,
    String whatsappCliente,
    StatusPedido status,
    Double valorTotal,
    List<ItemPedidoResponseDTO> itens,
    TipoEntrega tipoEntrega,
    String metodoPagamento,
    String cep,
    String endereco,
    String numero,
    String complemento,
    String bairro,
    String pixCopiaECola,
    String pixQrCodeBase64
) {}