package com.ledacosmeticos.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledacosmeticos.api.DTO.DadosAutenticacao;
import com.ledacosmeticos.api.DTO.DadosTokenJWT;
import com.ledacosmeticos.api.Model.Lojista;
import com.ledacosmeticos.api.Security.TokenService;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService; // Injetamos nosso serviço de token

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        // Pega o usuário que foi autenticado e gera o token para ele
        var tokenJWT = tokenService.gerarToken((Lojista) authentication.getPrincipal());
        
        // Devolve uma resposta 200 OK com o token no corpo da resposta
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}