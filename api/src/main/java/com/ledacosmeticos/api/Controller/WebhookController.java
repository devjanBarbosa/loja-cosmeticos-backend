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
    private PedidoService pedidoService;

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> receberNotificacaoMercadoPago(@RequestBody Map<String, Object> notificacao) {
        System.out.println(">>> WEBHOOK MERCADO PAGO RECEBIDO: " + notificacao);

        try {
            // 1. Validar o tipo de notificação (só nos interessam pagamentos)
            if ("payment".equals(notificacao.get("type"))) {

                // 2. Extrair o ID do pagamento de forma segura
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) notificacao.get("data");
                if (data != null && data.get("id") != null) {
                    String paymentId = data.get("id").toString();
                    System.out.println(">>> ID do Pagamento extraído: " + paymentId);

                    // 3. Delegar o processamento para o PedidoService
                    pedidoService.processarNotificacaoPagamento(paymentId);
                } else {
                     System.out.println(">>> Webhook recebido sem ID de pagamento.");
                }
            } else {
                 System.out.println(">>> Webhook ignorado. Tipo: " + notificacao.get("type"));
            }
             // 4. Retornar status 200 OK para confirmar o recebimento ao Mercado Pago
             return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("### ERRO CRÍTICO AO PROCESSAR WEBHOOK: " + e.getMessage());
            e.printStackTrace();
            // 5. Retornar um erro 500 para que o Mercado Pago tente reenviar a notificação.
            return ResponseEntity.internalServerError().build();
        }
    }
}
