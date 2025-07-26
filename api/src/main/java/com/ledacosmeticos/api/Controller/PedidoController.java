package com.ledacosmeticos.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledacosmeticos.api.Model.Pedido;
import com.ledacosmeticos.api.Service.PedidoService;

@CrossOrigin
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) {
        Pedido novoPedido = pedidoService.criar(pedido);
        // Retorna 201 Created com o pedido criado no corpo da resposta
        return ResponseEntity.status(201).body(novoPedido);
    }
}