package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.DTO.PedidoRequestDTO;
import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    // --- 1. Injeção de Dependência via Construtor (Melhor Prática) ---
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO pedidoDTO) { 
        try {
            // --- 2. Consistência de Tipos ---
            // O serviço agora retorna um DTO, e o controller repassa esse DTO.
            PedidoResponseDTO novoPedido = pedidoService.criar(pedidoDTO); 
            return ResponseEntity.status(201).body(novoPedido);

        } catch (RuntimeException e) {
            System.err.println("### ERRO NO CONTROLLER AO CRIAR PEDIDO: " + e.getMessage());
            // Retornar um corpo de erro padronizado é uma boa prática.
            // O frontend pode agora procurar pela chave "error".
            return ResponseEntity.badRequest().build();
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
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        try {
            String novoStatusStr = body.get("status");
            if (novoStatusStr == null || novoStatusStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            StatusPedido novoStatus = StatusPedido.valueOf(novoStatusStr.toUpperCase());
            PedidoResponseDTO pedidoAtualizado = pedidoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (IllegalArgumentException e) {
            // Erro específico para status inválido
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}