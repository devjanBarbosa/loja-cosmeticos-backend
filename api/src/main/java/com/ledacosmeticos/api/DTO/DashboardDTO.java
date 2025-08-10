package com.ledacosmeticos.api.DTO;

import java.util.List;
import java.util.Map;

public record DashboardDTO(
    double faturamentoTotal,
    long totalDePedidos,
    double ticketMedio,
    List<ProdutoMaisVendidoDTO> produtosMaisVendidos,
    List<Map<String, Object>> vendasPorCategoria,
    List<VendasDiariasDTO> vendasDiarias,
    List<UltimoPedidoDTO> ultimosPedidos,
    List<ProdutoEstoqueBaixoDTO> produtosComEstoqueBaixo
) {}