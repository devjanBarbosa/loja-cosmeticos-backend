package com.ledacosmeticos.api.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String nome;

    // --- CAMPO ADICIONADO ---
    @Enumerated(EnumType.STRING)
    private TipoCategoria tipo;

     private String urlImagem;
}