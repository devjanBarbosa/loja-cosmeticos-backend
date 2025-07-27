package com.ledacosmeticos.api.Controller;

import java.util.List; // Importe a List
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // Importe o CrossOrigin
import org.springframework.web.bind.annotation.GetMapping; // Importe o GetMapping
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledacosmeticos.api.DTO.PedidoResponseDTO;
import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200") // Permite acesso do nosso frontend
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) {
        Pedido novoPedido = pedidoService.criar(pedido);
        return ResponseEntity.status(201).body(novoPedido);
    }

    // --- NOVO ENDPOINT ABAIXO ---
   @GetMapping
public ResponseEntity<List<PedidoResponseDTO>> listarTodos() { 
    List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
    return ResponseEntity.ok(pedidos);
}
}