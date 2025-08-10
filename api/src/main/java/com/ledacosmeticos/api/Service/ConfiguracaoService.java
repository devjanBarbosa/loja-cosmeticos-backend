package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.Model.Configuracao;
import com.ledacosmeticos.api.Repository.ConfiguracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    public String getTaxaEntrega() {
        // Busca a configuração com a chave "taxa_entrega".
        // Se não encontrar, retorna um valor padrão "5.00" para evitar erros.
        return configuracaoRepository.findById("taxa_entrega")
                .map(Configuracao::getValor)
                .orElse("5.00");
    }
}