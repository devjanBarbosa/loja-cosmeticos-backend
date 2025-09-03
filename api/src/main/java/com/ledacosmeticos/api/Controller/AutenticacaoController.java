package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.DTO.DadosAutenticacao;
import com.ledacosmeticos.api.Model.Lojista;
import com.ledacosmeticos.api.Security.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api")
public class AutenticacaoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    // --- MÉTODO DE LOGIN ATUALIZADO ---
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid DadosAutenticacao dados, HttpServletResponse response) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);
        var lojista = (Lojista) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(lojista);

        // 1. Criar o cookie com o token
        Cookie cookie = new Cookie("auth_token", tokenJWT);
        cookie.setHttpOnly(true);      // O cookie não pode ser acedido por JavaScript
        cookie.setSecure(true);        // Enviar apenas sobre HTTPS (essencial em produção)
        cookie.setPath("/");           // Disponível em todo o site
        cookie.setMaxAge(60 * 60 * 8); // Expira em 8 horas

        // 2. Adicionar o cookie à resposta
        response.addCookie(cookie);

        // 3. Retornar uma resposta de sucesso sem o token no corpo
        return ResponseEntity.ok().build();
    }

    // --- NOVO ENDPOINT DE LOGOUT ---
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Cria um cookie com o mesmo nome, mas que expira imediatamente
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expira o cookie

        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hash/{senha}")
public String getHash(@PathVariable String senha) {
    return passwordEncoder.encode(senha);
}
}
