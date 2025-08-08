package com.ledacosmeticos.api.Model;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "pedidos") 
@Data 
@NoArgsConstructor
public class Pedido {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime dataDoPedido;
    
    // --- CAMPOS REMOVIDOS ---
    // private String nomeCliente;
    // private String whatsappCliente;
    
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    private Double valorTotal;
    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;

    // --- NOVA LIGAÇÃO ---
    // A anotação @ManyToOne diz que "Muitos Pedidos podem pertencer a Um Cliente".
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id") // Esta será a chave estrangeira na tabela de pedidos
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ItemPedido> itens;
}