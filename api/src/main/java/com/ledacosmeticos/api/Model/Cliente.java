package com.ledacosmeticos.api.Model;

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

    // Usaremos o WhatsApp como um identificador único para cada cliente.
    @Column(unique = true, nullable = false)
    private String whatsapp;

    // Um cliente pode ter vários pedidos.
    // O 'mappedBy = "cliente"' indica que a entidade Pedido é a dona desta relação.
    // O 'cascade = CascadeType.ALL' significa que as operações (como salvar) no Cliente
    // se propagarão para os seus Pedidos associados.
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;
}