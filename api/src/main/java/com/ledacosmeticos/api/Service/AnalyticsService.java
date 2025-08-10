package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.DTO.DashboardDTO;
import com.ledacosmeticos.api.DTO.ProdutoEstoqueBaixoDTO;
import com.ledacosmeticos.api.DTO.UltimoPedidoDTO;
import com.ledacosmeticos.api.DTO.VendasDiariasDTO;
import com.ledacosmeticos.api.DTO.ProdutoMaisVendidoDTO;
import com.ledacosmeticos.api.Repository.PedidoRepository;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardData(String periodo) {
        LocalDateTime dataInicio;
        switch (periodo.toLowerCase()) {
            case "dia":
                dataInicio = LocalDate.now().atStartOfDay();
                break;
            case "semana":
                dataInicio = LocalDate.now().minusDays(7).atStartOfDay();
                break;
            default:
                dataInicio = LocalDate.now().minusDays(30).atStartOfDay();
                break;
        }

        List<Object[]> vendasConcluidasPeriodo = pedidoRepository.findVendasConcluidasDesde(dataInicio);
        double faturamentoTotal = vendasConcluidasPeriodo.stream().mapToDouble(p -> (Double) p[1]).sum();
        long totalDePedidos = vendasConcluidasPeriodo.size();
        double ticketMedio = (totalDePedidos > 0) ? faturamentoTotal / totalDePedidos : 0;
        List<Map<String, Object>> vendasPorCategoria = pedidoRepository.findVendasPorCategoriaDesde(dataInicio);

        // --- CORREÇÃO AQUI ---
        // Agora chamamos o construtor do DTO independente
        List<ProdutoMaisVendidoDTO> produtosMaisVendidos = pedidoRepository.findProdutosMaisVendidosDesde(dataInicio).stream()
                .map(result -> new ProdutoMaisVendidoDTO( // Sem o "DashboardDTO."
                        (String) result[0], (long) result[1], (double) result[2]))
                .limit(5).collect(Collectors.toList());

        List<VendasDiariasDTO> vendasDiarias = pedidoRepository.findVendasDiariasDesde(dataInicio).stream()
                .map(result -> new VendasDiariasDTO(
                        result[0].toString(), (double) result[1]))
                .collect(Collectors.toList());

        List<UltimoPedidoDTO> ultimosPedidos = pedidoRepository.findTop5Recentes().stream()
                .map(p -> new UltimoPedidoDTO(
                        p.getId().toString(), p.getCliente().getNome(), p.getValorTotal(), p.getStatus().toString()))
                .collect(Collectors.toList());

        List<ProdutoEstoqueBaixoDTO> produtosComEstoqueBaixo = produtoRepository.findTop5ByEstoqueGreaterThanOrderByEstoqueAsc(0).stream()
                .filter(p -> p.getEstoque() <= 5)
                .map(p -> new ProdutoEstoqueBaixoDTO(
                        p.getId().toString(), p.getNome(), p.getEstoque()))
                .collect(Collectors.toList());

        return new DashboardDTO(
                faturamentoTotal, totalDePedidos, ticketMedio,
                produtosMaisVendidos, vendasPorCategoria, vendasDiarias,
                ultimosPedidos, produtosComEstoqueBaixo);
    }
}