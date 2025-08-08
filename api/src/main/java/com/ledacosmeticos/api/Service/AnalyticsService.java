package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.DTO.DashboardDTO;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardData() {
        // Buscamos apenas os pedidos que foram concluídos (ENTREGUE)
        List<Pedido> pedidosConcluidos = pedidoRepository.findByStatus(StatusPedido.ENTREGUE);

        // 1. Calcular Métricas Principais
        double faturamentoTotal = pedidosConcluidos.stream()
                .mapToDouble(Pedido::getValorTotal)
                .sum();
        
        long totalDePedidos = pedidosConcluidos.size();
        
        double ticketMedio = (totalDePedidos > 0) ? faturamentoTotal / totalDePedidos : 0;

        // 2. Calcular Vendas por Categoria
        List<Map<String, Object>> vendasPorCategoria = pedidoRepository.findVendasPorCategoria();

        // 3. Calcular Produtos Mais Vendidos
        List<DashboardDTO.ProdutoMaisVendidoDTO> produtosMaisVendidos = pedidoRepository.findProdutosMaisVendidos().stream()
                .map(result -> new DashboardDTO.ProdutoMaisVendidoDTO(
                        (String) result[0], // Nome do produto
                        (long) result[1],   // Quantidade vendida
                        (double) result[2]  // Faturamento gerado
                ))
                .limit(5) // Limitamos aos 5 produtos mais vendidos
                .collect(Collectors.toList());

        return new DashboardDTO(faturamentoTotal, totalDePedidos, ticketMedio, produtosMaisVendidos, vendasPorCategoria);
    }
}