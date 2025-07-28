package com.ledacosmeticos.api.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException; // Importação adicionada
import com.ledacosmeticos.api.Model.Lojista;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    // MELHORIA 1: Centralizar o issuer para evitar repetição
    private static final String ISSUER = "API Leda Cosmeticos";

    public String gerarToken(Lojista lojista) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER) // Usando a constante
                    .withSubject(lojista.getEmail())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            System.out.println("--- TokenService: Validando o token: " + tokenJWT);
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer(ISSUER) // Usando a constante
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } 
        // MELHORIA 2: Capturar exceções específicas para um log mais claro
        catch (TokenExpiredException exception) {
            // Este erro é comum e bom de tratar separadamente
            System.err.println("### TOKEN EXPIRADO: " + exception.getMessage());
            throw new RuntimeException("Token expirado!", exception);
        } catch (JWTVerificationException exception) {
            // Todos os outros erros de verificação (assinatura, etc.)
            System.err.println("### ERRO DE VERIFICAÇÃO JWT: " + exception.getMessage());
            throw new RuntimeException("Token JWT inválido!", exception);
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}