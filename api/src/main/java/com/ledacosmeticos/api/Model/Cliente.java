package com.ledacosmeticos.api.Model;

import com.fasterxml.jackson.annotation.JsonBackReference; // 1. Adicione esta importação
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String whatsapp;
    
    // Adicione este campo se ainda não o fez, é importante para o Mercado Pago
    private String email; 

    // ====================================================================
    // >>> A CORREÇÃO ESTÁ AQUI <<<
    // Adicionamos @JsonBackReference para quebrar o loop de serialização
    // ====================================================================
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Pedido> pedidos;
}