package com.ledacosmeticos.api.DTO;

import java.util.List;
import java.util.Map;

// Esta classe é um "pacote" que agrupa todas as informações do nosso dashboard.
public record DashboardDTO(
    // Métricas principais
    double faturamentoTotal,
    long totalDePedidos,
    double ticketMedio,

    // Detalhes dos produtos
    List<ProdutoMaisVendidoDTO> produtosMaisVendidos,

    // Detalhes das categorias
    List<Map<String, Object>> vendasPorCategoria
) {
    // DTO aninhado para representar um produto na lista dos mais vendidos.
    public record ProdutoMaisVendidoDTO(
        String nome,
        long quantidadeVendida,
        double faturamentoGerado
    ) {}
}