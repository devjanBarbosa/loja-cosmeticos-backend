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
import com.ledacosmeticos.api.Repository.LojistaRepository;
import com.ledacosmeticos.api.Model.Lojista;

import java.io.IOException;
import java.util.Optional;

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

        // Se um token for encontrado, tentamos autenticar o utilizador
        if (tokenJWT != null) {
            try {
                var subject = tokenService.getSubject(tokenJWT);
                Optional<Lojista> lojistaOpt = lojistaRepository.findByEmail(subject);
                if (lojistaOpt.isPresent()) {
                    Lojista lojista = lojistaOpt.get();
                    var authentication = new UsernamePasswordAuthenticationToken(lojista, null, lojista.getAuthorities());
                    // Colocamos o "crachá" de autenticação no contexto
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Se o token for inválido (expirado, etc.), simplesmente limpamos o contexto.
                // A autorização falhará mais tarde se a rota for protegida.
                System.err.println("Token JWT inválido ou expirado: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        // Independentemente de ter autenticado ou não, o pedido continua.
        // O SecurityConfig decidirá se um utilizador anónimo pode ou não aceder à rota.
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extrai apenas o token
        }
        return null;
    }
}