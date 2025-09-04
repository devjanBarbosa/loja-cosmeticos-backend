package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productName") String productName) {
        
        String fileName = fileStorageService.storeFile(file, productName);

        // --- CORREÇÃO PRINCIPAL AQUI ---
        // Agora retornamos apenas o caminho relativo para a imagem.
        // O frontend será responsável por saber como exibi-la.
        String relativePath = "/images/" + fileName;

        return ResponseEntity.ok(Map.of("url", relativePath));
    }
}
