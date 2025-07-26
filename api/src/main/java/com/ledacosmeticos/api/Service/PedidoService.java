package com.ledacosmeticos.api.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledacosmeticos.api.Model.ItemPedido;
import com.ledacosmeticos.api.Model.ItemPedidoId;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Repository.PedidoRepository;
import com.ledacosmeticos.api.Repository.ProdutoRepository;


@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository; 

   // Dentro de PedidoService.java

// Dentro de PedidoService.java

// Dentro de PedidoService.java

// Dentro de PedidoService.java

@Transactional
public Pedido criar(Pedido pedido) {
    
    // 1. Prepara o objeto Pedido principal
    pedido.setDataDoPedido(LocalDateTime.now()); 
    pedido.setStatus(StatusPedido.PENDENTE); 

    // 2. Prepara a lista de itens e o valor total
    List<ItemPedido> itens = pedido.getItens();
    Double valorTotalCalculado = 0.0;

    // 3. Loop para processar cada item do pedido
    for (ItemPedido item : itens) {
        
        // Busca o produto completo do banco de dados
        Produto produtoDoBanco = produtoRepository.findById(item.getProduto().getId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + item.getProduto().getId()));

        // --- INÍCIO DA CORREÇÃO DEFINITIVA ---
        
        // 4. Estabelece a ligação bi-direcional COMPLETA
        item.setPedido(pedido); // O filho agora conhece o pai
        item.setProduto(produtoDoBanco); // Substitui o produto "oco" pelo produto completo do banco
        
        // 5. Inicializa o ID composto. O JPA cuidará de preenchê-lo
        item.setId(new ItemPedidoId());
        
        // --- FIM DA CORREÇÃO ---

        // 6. Lógica de negócio
        if (produtoDoBanco.getEstoque() < item.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produtoDoBanco.getNome());
        }
        
        produtoDoBanco.setEstoque(produtoDoBanco.getEstoque() - item.getQuantidade());
        item.setPrecoUnitario(produtoDoBanco.getPreco());
        valorTotalCalculado += item.getPrecoUnitario() * item.getQuantidade();
    }

    // 7. Define o valor total e a lista de itens processada no Pedido
    pedido.setValorTotal(valorTotalCalculado);
    pedido.setItens(itens);
    
    // 8. Salva o pedido UMA ÚNICA VEZ.
    // O cascade fará com que os itens, agora corretamente ligados, sejam salvos junto.
 
    return pedidoRepository.save(pedido);
  }
}