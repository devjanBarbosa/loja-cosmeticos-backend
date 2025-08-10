package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired
    private PedidoService pedidoService; // Supondo que a lógica de confirmação ficará no PedidoService

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> receberNotificacaoMercadoPago(@RequestBody Map<String, Object> notificacao) {
        System.out.println(">>> WEBHOOK MERCADO PAGO RECEBIDO: " + notificacao);

        // Aqui você adicionaria a lógica para processar a notificação
        // Ex: pegar o ID do pagamento e chamar um serviço para confirmar o pedido.
        // String paymentId = notificacao.get("data").get("id").toString();
        // pedidoService.confirmarPedido(paymentId);

        return ResponseEntity.ok().build();
    }
}