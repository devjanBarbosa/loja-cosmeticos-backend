package com.ledacosmeticos.api.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Converte o caminho relativo (ex: ./uploads/) para um caminho absoluto
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String uploadPathString = uploadPath.toString().replace("\\", "/");

        System.out.println("--- [MvcConfig] Mapeando /images/** para a pasta física: " + "file:/" + uploadPathString + "/");

        // Esta linha cria a "ponte"
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/" + uploadPathString + "/");
    }
}