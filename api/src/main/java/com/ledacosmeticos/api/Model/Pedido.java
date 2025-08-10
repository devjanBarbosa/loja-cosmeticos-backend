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
    
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    
    private Double valorTotal;
    
    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;
    private String cep;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String metodoPagamento;


     // --- NOVOS CAMPOS PARA O PIX ---
    @Column(columnDefinition = "TEXT") // Para o código longo do "copia e cola"
    private String pixCopiaECola;

    @Column(columnDefinition = "TEXT") // Para a imagem do QR Code em base64
    private String pixQrCodeBase64;

    private String pixTransactionId; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ItemPedido> itens;
}