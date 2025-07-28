package com.ledacosmeticos.api.Security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ledacosmeticos.api.Model.Lojista;
import com.ledacosmeticos.api.Repository.LojistaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private LojistaRepository lojistaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("\n--- [SecurityFilter] INICIANDO FILTRO PARA A REQUISIÇÃO: " + request.getRequestURI());

        // ===== MUDANÇA PRINCIPAL =====
        // Verifica se a rota é a de login. Se for, o filtro não deve tentar validar um
        // token.
        // A rota de login deve ser sempre pública para permitir que o usuário se
        // autentique.
        if (request.getRequestURI().equals("/api/login")) {
            System.out.println("[SecurityFilter] Rota de login detectada. Bypass da validação de token.");
            filterChain.doFilter(request, response); // Apenas continua a cadeia de filtros
            return; // Encerra a execução deste filtro para esta requisição
        }
        // ============================

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            System.out.println("[SecurityFilter] Token JWT encontrado no cabeçalho.");
            try {
                var subject = tokenService.getSubject(tokenJWT);
                System.out.println("[SecurityFilter] Usuário (subject) extraído do token: " + subject);

                Optional<Lojista> lojistaOpt = lojistaRepository.findByEmail(subject);
                if (lojistaOpt.isPresent()) {
                    Lojista lojista = lojistaOpt.get();
                    System.out
                            .println("[SecurityFilter] Usuário encontrado no banco de dados: " + lojista.getUsername());
                    var authentication = new UsernamePasswordAuthenticationToken(lojista, null,
                            lojista.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("[SecurityFilter] >>> SUCESSO: Usuário autenticado no contexto de segurança!");
                } else {
                    System.out.println("[SecurityFilter] >>> FALHA: Usuário '" + subject
                            + "' do token não foi encontrado no banco de dados.");
                }
            } catch (Exception e) {
                System.out
                        .println("[SecurityFilter] >>> FALHA: Token JWT inválido ou expirado. Erro: " + e.getMessage());
            }
        } else {
            System.out.println("[SecurityFilter] Nenhum token JWT encontrado no cabeçalho Authorization.");
        }

        // Continua a cadeia de filtros para que a requisição chegue ao seu destino
        // (Controller)
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "").trim();
        }
        return null;
    }
}