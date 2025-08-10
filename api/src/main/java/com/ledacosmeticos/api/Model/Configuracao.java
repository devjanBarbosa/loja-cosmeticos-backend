package com.ledacosmeticos.api.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracoes")
@Data
@NoArgsConstructor
public class Configuracao {

    @Id
    private String chave; // Ex: "taxa_entrega"

    private String valor; // Ex: "5.00"
}