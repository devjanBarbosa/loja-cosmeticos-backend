package com.ledacosmeticos.api.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledacosmeticos.api.DTO.ItemPedidoResponseDTO;
import com.ledacosmeticos.api.DTO.PedidoRequestDTO;
import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.ItemPedido;
import com.ledacosmeticos.api.Model.ItemPedidoId;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Model.TipoEntrega;
import com.ledacosmeticos.api.Repository.PedidoRepository;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.ledacosmeticos.api.Repository.ClienteRepository;
import com.ledacosmeticos.api.Model.Cliente;

@Service
public class PedidoService {

     @Autowired
    private PixService pixService;


    @Autowired
    private ConfiguracaoService configuracaoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ClienteRepository clienteRepository;

@Transactional
    public Pedido criar(PedidoRequestDTO dto) {
        
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
        pedido.setMetodoPagamento(dto.metodoPagamento());

        if ("Delivery".equalsIgnoreCase(dto.metodoEntrega())) {
            pedido.setTipoEntrega(TipoEntrega.ENTREGA_LOCAL);
            pedido.setCep(dto.cep());
            pedido.setEndereco(dto.endereco());
            pedido.setNumero(dto.numero());
            pedido.setComplemento(dto.complemento());
            pedido.setBairro(dto.bairro());
        } else {
            pedido.setTipoEntrega(TipoEntrega.RETIRADA_NA_LOJA);
        }
        
        List<ItemPedido> itens = dto.itens().stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(UUID.fromString(itemDto.produtoId()))
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + itemDto.produtoId()));

            if (produto.getEstoque() < itemDto.quantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            
            produto.setEstoque(produto.getEstoque() - itemDto.quantidade());

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDto.quantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());
            itemPedido.setPedido(pedido);
            itemPedido.setId(new ItemPedidoId());
            
            return itemPedido;
        }).collect(Collectors.toList());

        double valorTotalCalculado = itens.stream()
                .mapToDouble(item -> item.getPrecoUnitario() * item.getQuantidade())
                .sum();
        
         if (pedido.getTipoEntrega() == TipoEntrega.ENTREGA_LOCAL) {
            double taxaEntrega = Double.parseDouble(configuracaoService.getTaxaEntrega());
            valorTotalCalculado += taxaEntrega;
        }

        pedido.setItens(itens);
        pedido.setValorTotal(valorTotalCalculado);
        
        // Salva o pedido uma primeira vez para gerar o ID
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Se o método de pagamento for "Pix", chama o serviço de pagamento
        if ("Pix".equalsIgnoreCase(dto.metodoPagamento())) {
            
            // ====================================================================
            // >>> A CORREÇÃO ESTÁ AQUI: ADICIONAMOS O TRY-CATCH <<<
            // ====================================================================
            try {
                Payment pagamento = pixService.criarCobrancaPix(pedidoSalvo);
                
                String qrCodeBase64 = pagamento.getPointOfInteraction().getTransactionData().getQrCodeBase64();
                String copiaECola = pagamento.getPointOfInteraction().getTransactionData().getQrCode();
                
                pedidoSalvo.setPixQrCodeBase64("data:image/png;base64," + qrCodeBase64);
                pedidoSalvo.setPixCopiaECola(copiaECola);
                pedidoSalvo.setPixTransactionId(pagamento.getId().toString());

                return pedidoRepository.save(pedidoSalvo);

            } catch (MPException | MPApiException e) {
                // Se a API do Mercado Pago falhar, o pedido não será concluído.
                System.err.println("### ERRO AO GERAR PIX: A transação será revertida (rollback). ###");
                e.printStackTrace();
                // Lançamos uma RuntimeException para que a anotação @Transactional desfaça o salvamento do pedido.
                throw new RuntimeException("Falha ao comunicar com o gateway de pagamento. Tente novamente.", e);
            }
        }

        return pedidoSalvo;
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
                itemDtos,
                pedido.getTipoEntrega(),
                pedido.getMetodoPagamento(),
                pedido.getCep(),
                pedido.getEndereco(),
                pedido.getNumero(),
                pedido.getComplemento(),
                pedido.getBairro(),
                pedido.getPixCopiaECola(),
                pedido.getPixQrCodeBase64()
        );
    }
}