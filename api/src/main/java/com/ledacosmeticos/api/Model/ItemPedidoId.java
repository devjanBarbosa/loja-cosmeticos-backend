package com.ledacosmeticos.api.Model;
import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable @Data
public class ItemPedidoId implements Serializable {
    private UUID pedidoId;
    private UUID produtoId;
}