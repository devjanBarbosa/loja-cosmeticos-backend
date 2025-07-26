package com.ledacosmeticos.api.Model;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "produtos") @Data @NoArgsConstructor
public class Produto {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;
    private String descricao;
    private Double preco;
    private String urlImagem;
    private Integer estoque;

    @ManyToOne @JoinColumn(name = "categoria_id")
    
    private Categoria categoria;
}