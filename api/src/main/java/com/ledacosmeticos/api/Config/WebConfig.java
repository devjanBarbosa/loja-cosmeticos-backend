package com.ledacosmeticos.api.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração CORS adicional a nível de MVC
 * Esta configuração trabalha em conjunto com a do SecurityConfig
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("=== CONFIGURANDO CORS NO WEBMVC ===");
        
        registry.addMapping("/**")
                // Permite requisições do Angular
                .allowedOriginPatterns("http://localhost:4200")
                // Métodos HTTP permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                // Headers permitidos
                .allowedHeaders("*")
                // Headers expostos
                .exposedHeaders("Authorization", "Content-Type")
                // Permite credenciais (cookies, authorization headers, etc)
                .allowCredentials(true)
                // Cache por 1 hora
                .maxAge(3600);
    }
}