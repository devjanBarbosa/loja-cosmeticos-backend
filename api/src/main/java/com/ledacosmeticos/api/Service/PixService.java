package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.Model.Cliente;
import com.ledacosmeticos.api.Model.Pedido;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PixService {

    @Value("${mercadopago.access_token}")
    private String mercadoPagoAccessToken;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
    }

    

    // ====================================================================
    // >>> A CORREÇÃO ESTÁ NA ASSINATURA DO MÉTODO ABAIXO <<<
    // ====================================================================
    public Payment criarCobrancaPix(Pedido pedido) throws MPException, MPApiException {
        
        PaymentClient client = new PaymentClient();

        Cliente clienteDoPedido = pedido.getCliente();
        String nomeCliente = clienteDoPedido.getNome();
        // O Mercado Pago recomenda um e-mail. Usaremos um genérico se o cliente não tiver um.
        String emailCliente = (clienteDoPedido.getEmail() != null && !clienteDoPedido.getEmail().isEmpty())
            ? clienteDoPedido.getEmail()
            : "cliente+" + clienteDoPedido.getWhatsapp() + "@ledacosmeticos.com";

        PaymentCreateRequest createRequest =
            PaymentCreateRequest.builder()
                .transactionAmount(new BigDecimal(pedido.getValorTotal()))
                .description("Pedido #" + pedido.getId().toString().substring(0, 8) + " - Leda Cosméticos")
                .paymentMethodId("pix")
                .payer(
                    PaymentPayerRequest.builder()
                        .email(emailCliente)
                        .firstName(nomeCliente.split(" ")[0]) // Pega o primeiro nome
                        .build())
                .build();

        // Agora o método está ciente de que esta linha pode lançar uma exceção
        return client.create(createRequest);
    }
}