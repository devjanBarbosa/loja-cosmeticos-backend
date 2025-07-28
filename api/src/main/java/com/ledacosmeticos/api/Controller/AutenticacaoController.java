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
@RequestMapping("/api")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService; // Injetamos nosso serviço de token

   // Dentro de AutenticacaoController.java
@PostMapping("/login")
public ResponseEntity efetuarLogin(@RequestBody DadosAutenticacao dados) {
    System.out.println("\n--- [AutenticacaoController] TENTATIVA DE LOGIN RECEBIDA PARA O EMAIL: " + dados.email());
    var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
    var authentication = manager.authenticate(authenticationToken);

    var tokenJWT = tokenService.gerarToken((Lojista) authentication.getPrincipal());
    System.out.println("--- [AutenticacaoController] TOKEN GERADO COM SUCESSO ---");

    return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
}
}