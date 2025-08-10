package com.ledacosmeticos.api.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens i JOIN FETCH i.produto JOIN FETCH p.cliente WHERE p.id = :id")
    Optional<Pedido> findByIdWithItensAndProdutos(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Pedido p JOIN FETCH p.itens i JOIN FETCH i.produto JOIN FETCH p.cliente")
    List<Pedido> findAllWithItensAndProdutos();

    List<Pedido> findByStatus(StatusPedido status);

    // --- QUERIES PARA O DASHBOARD ---
   @Query("SELECT p.id, p.valorTotal FROM Pedido p WHERE p.status = 'ENTREGUE' AND p.dataDoPedido >= :dataInicio")
    List<Object[]> findVendasConcluidasDesde(@Param("dataInicio") LocalDateTime dataInicio);

    @Query("SELECT new map(c.nome as nome, SUM(ip.precoUnitario * ip.quantidade) as totalVendido) " +
           "FROM Pedido p JOIN p.itens ip JOIN ip.produto pr JOIN pr.categoria c " +
           "WHERE p.status = 'ENTREGUE' AND p.dataDoPedido >= :dataInicio " +
           "GROUP BY c.nome ORDER BY totalVendido DESC")
    List<Map<String, Object>> findVendasPorCategoriaDesde(@Param("dataInicio") LocalDateTime dataInicio);

    @Query("SELECT pr.nome, SUM(ip.quantidade) as quantidadeVendida, SUM(ip.precoUnitario * ip.quantidade) as faturamentoGerado " +
           "FROM Pedido p JOIN p.itens ip JOIN ip.produto pr " +
           "WHERE p.status = 'ENTREGUE' AND p.dataDoPedido >= :dataInicio " +
           "GROUP BY pr.nome ORDER BY quantidadeVendida DESC")
    List<Object[]> findProdutosMaisVendidosDesde(@Param("dataInicio") LocalDateTime dataInicio);
    
    // Query corrigida para agrupar por dia
    @Query("SELECT FUNCTION('date', p.dataDoPedido) as dia, SUM(p.valorTotal) as total " +
           "FROM Pedido p " +
           "WHERE p.status = 'ENTREGUE' AND p.dataDoPedido >= :dataInicio " +
           "GROUP BY dia ORDER BY dia ASC")
    List<Object[]> findVendasDiariasDesde(@Param("dataInicio") LocalDateTime dataInicio);
    
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.status NOT IN ('ENTREGUE', 'CANCELADO') ORDER BY p.dataDoPedido DESC LIMIT 5")
    List<Pedido> findTop5Recentes();
}