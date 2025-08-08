package com.ledacosmeticos.api.Repository;

import java.util.List; // Certifique-se que esta importação existe
import java.util.Map;
import java.util.Optional; // Certifique-se que esta importação existe
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- NOVA IMPORTAÇÃO
import org.springframework.data.repository.query.Param; // <-- NOVA IMPORTAÇÃO

import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    // --- NOVOS MÉTODOS COM JOIN FETCH ---

    // Este método busca um pedido por ID e já traz todos os seus itens e produtos.
    @Query("SELECT p FROM Pedido p JOIN FETCH p.itens i JOIN FETCH i.produto WHERE p.id = :id")
    Optional<Pedido> findByIdWithItensAndProdutos(@Param("id") UUID id);

    // Este método busca TODOS os pedidos e já traz todos os seus itens e produtos.
    // Evita o problema de N+1 queries.
    @Query("SELECT DISTINCT p FROM Pedido p JOIN FETCH p.itens i JOIN FETCH i.produto")
    List<Pedido> findAllWithItensAndProdutos();

    List<Pedido> findByStatus(StatusPedido status);

    // --- NOVA QUERY PARA VENDAS POR CATEGORIA ---
    @Query("SELECT new map(c.nome as nome, SUM(ip.precoUnitario * ip.quantidade) as totalVendido) " +
           "FROM Pedido p JOIN p.itens ip JOIN ip.produto pr JOIN pr.categoria c " +
           "WHERE p.status = 'ENTREGUE' " +
           "GROUP BY c.nome " +
           "ORDER BY totalVendido DESC")
    List<Map<String, Object>> findVendasPorCategoria();

    // --- NOVA QUERY PARA PRODUTOS MAIS VENDIDOS ---
    @Query("SELECT pr.nome, SUM(ip.quantidade) as quantidadeVendida, SUM(ip.precoUnitario * ip.quantidade) as faturamentoGerado " +
           "FROM Pedido p JOIN p.itens ip JOIN ip.produto pr " +
           "WHERE p.status = 'ENTREGUE' " +
           "GROUP BY pr.nome " +
           "ORDER BY quantidadeVendida DESC")
    List<Object[]> findProdutosMaisVendidos();
}
