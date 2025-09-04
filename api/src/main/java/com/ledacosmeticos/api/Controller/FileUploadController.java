package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Service.S3Service; // 1. Importe o novo serviço S3
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    // 2. Mude a dependência de FileStorageService para S3Service
    private final S3Service s3Service;

    // 3. Atualize o construtor para injetar o S3Service
    public FileUploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productName") String productName) {
        
        // 4. Chame o método do S3Service para fazer o upload
        String fileUrl = s3Service.uploadFile(file, productName);

        // 5. O S3Service já retorna o URL público completo da imagem.
        //    Basta retorná-lo diretamente para o frontend.
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}

