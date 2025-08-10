package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.DTO.PedidoRequestDTO;
import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Adicione esta importação
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // ====================================================================
    // >>> MÉTODO criarPedido CORRIGIDO <<<
    // ====================================================================
    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoRequestDTO pedidoDTO) { 
        try {
            // A chamada ao serviço permanece a mesma
            Pedido novoPedido = pedidoService.criar(pedidoDTO); 
            // Retorna o pedido completo com os dados do PIX (se aplicável)
            return ResponseEntity.status(201).body(novoPedido);

        } catch (RuntimeException e) {
            // Se o PedidoService lançar uma exceção (ex: falha ao gerar PIX), nós a capturamos aqui.
            System.err.println("### ERRO NO CONTROLLER AO CRIAR PEDIDO: " + e.getMessage());
            e.printStackTrace();
            
            // Retornamos uma resposta de erro clara para o frontend.
            // Usamos um Map para criar um objeto JSON de erro: { "error": "mensagem de erro" }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); 
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable UUID id) {
        try {
            PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(@PathVariable UUID id, @RequestBody java.util.Map<String, String> body) {
        try {
            String novoStatusStr = body.get("status");
            if (novoStatusStr == null || novoStatusStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            StatusPedido novoStatus = StatusPedido.valueOf(novoStatusStr.toUpperCase());
            PedidoResponseDTO pedidoAtualizado = pedidoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}