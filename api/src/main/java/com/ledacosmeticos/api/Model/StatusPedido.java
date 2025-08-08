package com.ledacosmeticos.api.Model;

public enum StatusPedido {
    PENDENTE,           // O pedido foi recebido, mas ainda não foi visto/processado.
    EM_PREPARACAO,      // O pedido está sendo separado e embalado.
    PRONTO_PARA_RETIRADA, // O pedido está pronto, aguardando o cliente buscar na loja.
    SAIU_PARA_ENTREGA,  // O pedido saiu com o entregador.
    ENTREGUE,           // O cliente já recebeu o pedido.
    CANCELADO;          // O pedido foi cancelado.
}