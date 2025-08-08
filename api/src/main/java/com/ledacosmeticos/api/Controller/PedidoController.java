package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Model.StatusPedido;
import com.ledacosmeticos.api.Service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    // --- CORREÇÃO AQUI ---
    // Adicionámos o nome "pedidoDTO" à variável
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoRequestDTO pedidoDTO) { 
        try {
            // Agora a variável 'pedidoDTO' existe e pode ser usada
            Pedido novoPedido = pedidoService.criar(pedidoDTO); 
            return ResponseEntity.status(201).body(novoPedido);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null); 
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
        // ... (o resto do seu controller está correto e não precisa de alterações)
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