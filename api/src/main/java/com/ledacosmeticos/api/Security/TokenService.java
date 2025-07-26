package com.ledacosmeticos.api.Security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.ledacosmeticos.api.Model.Lojista;

@Service
public class TokenService {

    // Pega o valor da nossa palavra secreta do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Lojista lojista) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Leda Cosmeticos") // Quem está emitindo o token
                    .withSubject(lojista.getEmail()) // O "dono" do token
                    .withExpiresAt(dataExpiracao()) // Define o prazo de validade
                    .sign(algoritmo); // Assina o token com nosso algoritmo e segredo
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    private Instant dataExpiracao() {
        // Define o token para expirar em 2 horas a partir de agora
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
    // Dentro da classe TokenService.java

public String getSubject(String tokenJWT) {
    try {
        var algoritmo = Algorithm.HMAC256(secret);
        return JWT.require(algoritmo)
            .withIssuer("API Leda Cosmeticos")
            .build()
            .verify(tokenJWT)
            .getSubject();
    } catch (Exception exception) {
        throw new RuntimeException("Token JWT inválido ou expirado!");
    }
}
}