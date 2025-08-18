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
    
    // CORRIGIDO: Nome do campo alinhado com o DTO e o frontend
    private LocalDateTime dataCriacao;
    
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    
    // ADICIONADO: Campo para o subtotal
    private Double subtotal;

    // ADICIONADO: Campo para a taxa de entrega
    private Double taxaEntrega;
    
    private Double valorTotal;
    
    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;
    
    private String metodoPagamento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;

    // --- CAMPOS PARA O PIX ---
    @Column(columnDefinition = "TEXT")
    private String pixCopiaECola;

    @Column(columnDefinition = "TEXT")
    private String pixQrCodeBase64;

    private String pixTransactionId; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ItemPedido> itens;
}
