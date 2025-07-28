package com.ledacosmeticos.api.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ledacosmeticos.api.Repository.LojistaRepository; // Corrigindo para o seu repositório
import com.ledacosmeticos.api.Model.Lojista; // Corrigindo para o seu modelo

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private LojistaRepository lojistaRepository;

    // --- NOVA LISTA DE ROTAS PÚBLICAS ---
    private final List<String> rotasPublicasGet = Arrays.asList(
            "/api/produtos",
            "/api/categorias",
            "/images"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // --- LÓGICA DE BYPASS MELHORADA ---
        if (isPublicRoute(request)) {
            System.out.println("[SecurityFilter] Rota pública detectada. Bypass do filtro: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        // --- FIM DA MELHORIA ---

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                var subject = tokenService.getSubject(tokenJWT);
                // Corrigido para usar o seu LojistaRepository
                Optional<Lojista> lojistaOpt = lojistaRepository.findByEmail(subject);
                if (lojistaOpt.isPresent()) {
                    Lojista lojista = lojistaOpt.get();
                    var authentication = new UsernamePasswordAuthenticationToken(lojista, null, lojista.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Não faz nada, apenas não autentica. A autorização será tratada depois.
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "").trim();
        }
        return null;
    }

    // --- NOVO MÉTODO PARA VERIFICAR SE A ROTA É PÚBLICA ---
    private boolean isPublicRoute(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (method.equalsIgnoreCase("POST") && path.equals("/api/login")) {
            return true;
        }

        if (method.equalsIgnoreCase("POST") && path.equals("/api/pedidos")) {
            return true;
        }

        if (method.equalsIgnoreCase("GET")) {
            return rotasPublicasGet.stream().anyMatch(path::startsWith);
        }

        return false;
    }
}