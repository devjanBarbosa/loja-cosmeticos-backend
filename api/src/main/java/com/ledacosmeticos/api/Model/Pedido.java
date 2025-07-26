package com.ledacosmeticos.api.Model;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "pedidos") @Data @NoArgsConstructor
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime dataDoPedido;
    private String nomeCliente;
    private String whatsappCliente;
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    private Double valorTotal;
    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;
}