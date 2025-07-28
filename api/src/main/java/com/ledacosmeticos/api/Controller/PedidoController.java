package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.Pedido;
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
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) {
        // --- INÍCIO DA MELHORIA ---
        try {
            Pedido novoPedido = pedidoService.criar(pedido);
            return ResponseEntity.status(201).body(novoPedido);
        } catch (RuntimeException e) {
            // Este log vai mostrar-nos a causa exata do erro!
            System.err.println("### ERRO AO CRIAR PEDIDO: " + e.getMessage());
            e.printStackTrace(); // Imprime o stack trace completo para depuração
            // Retorna uma resposta de erro 400 (Bad Request), que é mais apropriada
            return ResponseEntity.badRequest().body(null); 
        }
        // --- FIM DA MELHORIA ---
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable UUID id) {
    PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
    return ResponseEntity.ok(pedido);
}
}