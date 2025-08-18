package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.DTO.ItemPedidoResponseDTO;
import com.ledacosmeticos.api.DTO.PedidoRequestDTO;
import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.*; // Importa todos os modelos de uma vez
import com.ledacosmeticos.api.Repository.ClienteRepository;
import com.ledacosmeticos.api.Repository.PedidoRepository;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // 1. Use BigDecimal para cálculos monetários
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    // --- 2. Injeção de Dependência via Construtor (Melhor Prática) ---
    private final PixService pixService;
    private final ConfiguracaoService configuracaoService;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PixService pixService, ConfiguracaoService configuracaoService, PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, ClienteRepository clienteRepository) {
        this.pixService = pixService;
        this.configuracaoService = configuracaoService;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        // Busca ou cria o cliente
        Cliente cliente = findOrCreateCliente(dto);
        
        // Monta o objeto Pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setMetodoPagamento(dto.metodoPagamento());

        if ("Delivery".equalsIgnoreCase(dto.metodoEntrega())) {
            pedido.setTipoEntrega(TipoEntrega.ENTREGA_LOCAL);
            pedido.setEndereco(criarEndereco(dto));
        } else {
            pedido.setTipoEntrega(TipoEntrega.RETIRADA_NA_LOJA);
        }
        
        // Processa os itens e calcula os totais
        processarItens(pedido, dto.itens());
        calcularTotais(pedido);
        
        // Salva o pedido inicial
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Gera o PIX, se necessário
        if ("Pix".equalsIgnoreCase(dto.metodoPagamento())) {
            gerarPixParaPedido(pedidoSalvo);
        }

        // --- 3. Retorna o DTO, não a Entidade ---
        return convertToDto(pedidoSalvo);
    }

    // --- 4. Métodos Privados para Organizar a Lógica ---

    private Cliente findOrCreateCliente(PedidoRequestDTO dto) {
        return clienteRepository.findByWhatsapp(dto.whatsappCliente())
                .orElseGet(() -> {
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNome(dto.nomeCliente());
                    novoCliente.setWhatsapp(dto.whatsappCliente());
                    return clienteRepository.save(novoCliente);
                });
    }
    
    private Endereco criarEndereco(PedidoRequestDTO dto) {
        Endereco endereco = new Endereco();
        endereco.setCep(dto.cep());
        endereco.setLogradouro(dto.endereco());
        endereco.setNumero(dto.numero());
        endereco.setComplemento(dto.complemento());
        endereco.setBairro(dto.bairro());
        return endereco;
    }

    private void processarItens(Pedido pedido, List<PedidoRequestDTO.ItemDTO> itemDtos) {
        List<ItemPedido> itens = itemDtos.stream().map(itemDto -> {
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
            itemPedido.setId(new ItemPedidoId()); // Mantido para compatibilidade com sua estrutura
            
            return itemPedido;
        }).collect(Collectors.toList());
        
        pedido.setItens(itens);
    }

    private void calcularTotais(Pedido pedido) {
        BigDecimal subtotal = pedido.getItens().stream()
            .map(item -> BigDecimal.valueOf(item.getPrecoUnitario()).multiply(BigDecimal.valueOf(item.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal taxaEntrega = BigDecimal.ZERO;
        if (pedido.getTipoEntrega() == TipoEntrega.ENTREGA_LOCAL) {
            taxaEntrega = new BigDecimal(configuracaoService.getTaxaEntrega());
        }

        pedido.setSubtotal(subtotal.doubleValue());
        pedido.setTaxaEntrega(taxaEntrega.doubleValue());
        pedido.setValorTotal(subtotal.add(taxaEntrega).doubleValue());
    }

    private void gerarPixParaPedido(Pedido pedido) {
        try {
            Payment pagamento = pixService.criarCobrancaPix(pedido);
            pedido.setPixQrCodeBase64("data:image/png;base64," + pagamento.getPointOfInteraction().getTransactionData().getQrCodeBase64());
            pedido.setPixCopiaECola(pagamento.getPointOfInteraction().getTransactionData().getQrCode());
            pedido.setPixTransactionId(pagamento.getId().toString());
            pedidoRepository.save(pedido); // Salva as informações do PIX no pedido
        } catch (MPException | MPApiException e) {
            System.err.println("### ERRO AO GERAR PIX: A transação será revertida (rollback). ###");
            e.printStackTrace();
            throw new RuntimeException("Falha ao comunicar com o gateway de pagamento. Tente novamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + id));
        return convertToDto(pedido);
    }


    @Transactional
    public PedidoResponseDTO atualizarStatus(UUID id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + id));
        
        pedido.setStatus(novoStatus);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return convertToDto(pedidoSalvo);
    }

    // Dentro da classe PedidoService

private PedidoResponseDTO convertToDto(Pedido pedido) {
    List<ItemPedidoResponseDTO> itensDto = pedido.getItens().stream()
        .map(item -> new ItemPedidoResponseDTO(
            item.getProduto().getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario()
        ))
        .collect(Collectors.toList());

    // --- Verificação de Segurança para o Cliente ---
    String nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome() : "Cliente não informado";
    String whatsappCliente = pedido.getCliente() != null ? pedido.getCliente().getWhatsapp() : "N/A";

    // --- Verificação de Segurança para o Endereço ---
    // Esta é a correção principal para o erro que você encontrou.
    String cep = pedido.getEndereco() != null ? pedido.getEndereco().getCep() : null;
    String endereco = pedido.getEndereco() != null ? pedido.getEndereco().getLogradouro() : null;
    String numero = pedido.getEndereco() != null ? pedido.getEndereco().getNumero() : null;
    String complemento = pedido.getEndereco() != null ? pedido.getEndereco().getComplemento() : null;
    String bairro = pedido.getEndereco() != null ? pedido.getEndereco().getBairro() : null;

    // OBS: Note que seu DTO tem o campo "dataCriacao", mas a entidade Pedido
    // tem "dataDoPedido". Estou usando "getDataDoPedido()" para alinhar com a entidade.
    return new PedidoResponseDTO(
        pedido.getId().toString(),
        pedido.getDataCriacao(),
        nomeCliente,
        whatsappCliente,
        pedido.getStatus(),
        pedido.getSubtotal(),
        pedido.getTaxaEntrega(),
        pedido.getValorTotal(),
        itensDto,
        pedido.getTipoEntrega(), // Alinhado com a entidade Pedido
        pedido.getMetodoPagamento(),
        cep,
        endereco,
        numero,
        complemento,
        bairro,
        pedido.getPixCopiaECola(),
        pedido.getPixQrCodeBase64()
    );
}

     @Transactional
    public void processarNotificacaoPagamento(String paymentId) {
        try {
            // 1. Busca os detalhes do pagamento na API do Mercado Pago para confirmar o status
            Payment pagamento = pixService.buscarPagamentoPorId(paymentId);

            // 2. Verifica se o pagamento foi aprovado
            if (pagamento != null && "approved".equals(pagamento.getStatus())) {
                System.out.println(">>> Pagamento ID " + paymentId + " foi aprovado.");

                // 3. Encontra o pedido correspondente no nosso banco de dados
                Pedido pedido = pedidoRepository.findByPixTransactionId(paymentId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado para o paymentId: " + paymentId));

                // 4. Atualiza o status do pedido, se ele ainda estiver pendente
                if (pedido.getStatus() == StatusPedido.PENDENTE) {
                    pedido.setStatus(StatusPedido.EM_PREPARACAO);
                    pedidoRepository.save(pedido);
                    System.out.println(">>> Pedido ID " + pedido.getId() + " atualizado para EM PREPARAÇÃO.");
                } else {
                    System.out.println(">>> Pedido ID " + pedido.getId() + " já foi processado. Status atual: " + pedido.getStatus());
                }
            } else {
                System.out.println(">>> Pagamento ID " + paymentId + " não foi aprovado. Status: " + (pagamento != null ? pagamento.getStatus() : "N/A"));
                // Opcional: Implementar lógica para pagamentos rejeitados (ex: cancelar pedido)
            }

        } catch (Exception e) {
            System.err.println("### ERRO ao processar notificação para o paymentId " + paymentId + ": " + e.getMessage());
            e.printStackTrace();
            // Lançar uma exceção garante que, se algo falhar, a transação seja desfeita.
            throw new RuntimeException("Falha ao processar notificação de pagamento.", e);
        }
    }
}
