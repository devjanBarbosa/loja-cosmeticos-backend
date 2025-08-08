package com.ledacosmeticos.api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledacosmeticos.api.Controller.PedidoRequestDTO;
import com.ledacosmeticos.api.DTO.ItemPedidoResponseDTO;
import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.ItemPedido;
import com.ledacosmeticos.api.Model.ItemPedidoId;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Repository.PedidoRepository;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import com.ledacosmeticos.api.Repository.ClienteRepository;
import com.ledacosmeticos.api.Model.Cliente;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ClienteRepository clienteRepository;

   @Transactional
public Pedido criar(PedidoRequestDTO dto) {
    
    // 1. Lógica do Cliente (encontrar ou criar)
    Cliente cliente = clienteRepository.findByWhatsapp(dto.whatsappCliente())
            .orElseGet(() -> {
                Cliente novoCliente = new Cliente();
                novoCliente.setNome(dto.nomeCliente());
                novoCliente.setWhatsapp(dto.whatsappCliente());
                return clienteRepository.save(novoCliente);
            });
    
    Pedido pedido = new Pedido();
    pedido.setCliente(cliente);
    pedido.setDataDoPedido(LocalDateTime.now()); 
    pedido.setStatus(StatusPedido.PENDENTE); 

    // Mapeia a lista de DTOs para uma lista de Entidades ItemPedido
    List<ItemPedido> itens = dto.itens().stream().map(itemDto -> {
        Produto produto = produtoRepository.findById(UUID.fromString(itemDto.produtoId())) // Usamos itemDto.produtoId()
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + itemDto.produtoId()));

        if (produto.getEstoque() < itemDto.quantidade()) { // Usamos itemDto.quantidade()
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }
        
        produto.setEstoque(produto.getEstoque() - itemDto.quantidade());

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setProduto(produto);
        itemPedido.setQuantidade(itemDto.quantidade());
        itemPedido.setPrecoUnitario(produto.getPreco());
        itemPedido.setPedido(pedido);
        itemPedido.setId(new ItemPedidoId()); // Inicializa o ID composto
        
        return itemPedido;
    }).collect(Collectors.toList());

    // Calcula o valor total
    double valorTotalCalculado = itens.stream()
            .mapToDouble(item -> item.getPrecoUnitario() * item.getQuantidade())
            .sum();

    pedido.setItens(itens);
    pedido.setValorTotal(valorTotalCalculado);

    return pedidoRepository.save(pedido);
}
    // --- MÉTODO ATUALIZADO ---
    @Transactional(readOnly = true) // Boa prática para métodos de leitura
    public List<PedidoResponseDTO> listarTodos() {
        // Usamos o nosso novo método de busca eficiente
        List<Pedido> pedidos = pedidoRepository.findAllWithItensAndProdutos();
        return pedidos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- MÉTODO ATUALIZADO ---
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(UUID id) {
        // Usamos o nosso novo método que já carrega tudo
        Pedido pedido = pedidoRepository.findByIdWithItensAndProdutos(id)
                .orElseThrow(() -> new RuntimeException("Encomenda não encontrada com ID: " + id));
        return convertToDto(pedido);
    }

    // --- MÉTODO ATUALIZADO E FINAL ---
    @Transactional
    public PedidoResponseDTO atualizarStatus(UUID id, StatusPedido novoStatus) {
        // Usamos a busca normal aqui, pois só precisamos de mudar o status
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encomenda não encontrada com ID: " + id));

        pedido.setStatus(novoStatus);
        
        // Após salvar, buscamos novamente usando o método completo para retornar o DTO atualizado
        Pedido pedidoAtualizadoECompleto = pedidoRepository.findByIdWithItensAndProdutos(id).get();

        return convertToDto(pedidoAtualizadoECompleto);
    }

    // --- CONVERSOR SIMPLIFICADO ---
    // Já não precisa das chamadas `Hibernate.initialize` porque os dados já vêm carregados
    private PedidoResponseDTO convertToDto(Pedido pedido) {
        List<ItemPedidoResponseDTO> itemDtos = pedido.getItens().stream()
                .map(item -> new ItemPedidoResponseDTO(
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario()))
                .collect(Collectors.toList());

         return new PedidoResponseDTO(
                pedido.getId().toString(),
                pedido.getDataDoPedido(),
                pedido.getCliente().getNome(),
                pedido.getCliente().getWhatsapp(),
                pedido.getStatus(),
                pedido.getValorTotal(),
                itemDtos
        );
    }
}