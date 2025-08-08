package com.ledacosmeticos.api.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // --- ADICIONE ESTA ANOTAÇÃO AQUI ---
    @Column(columnDefinition = "TEXT")
    private String nome;
    
    // ... resto do seu ficheiro ...
    private String descricao;
    private Double preco;
    private String urlImagem;
    private Integer estoque;
    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}