package com.ledacosmeticos.api.Security;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Importe UserDetails
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
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

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            // Usamos UserDetails, que é o tipo de retorno do nosso repositório
            UserDetails lojista = lojistaRepository.findByEmail(subject);

            // --- A VERIFICAÇÃO DE SEGURANÇA QUE FALTAVA ---
            if (lojista != null) {
                // Se o usuário existe, nós o autenticamos na sessão atual
                var authentication = new UsernamePasswordAuthenticationToken(lojista, null, lojista.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua a cadeia de filtros, com ou sem autenticação
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