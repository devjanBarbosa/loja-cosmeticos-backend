package com.ledacosmeticos.api.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório para armazenar os ficheiros.", ex);
        }
    }

    // --- MÉTODO ATUALIZADO ---
    // Agora ele recebe o nome do produto para criar um nome de ficheiro amigável
    public String storeFile(MultipartFile file, String productName) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Cria um nome de ficheiro "slugificado" e adiciona um hash curto para garantir que seja único
        String slug = slugify(productName);
        String uniqueHash = UUID.randomUUID().toString().substring(0, 6);
        String fileName = slug + "-" + uniqueHash + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível armazenar o ficheiro " + fileName + ". Por favor, tente novamente!", ex);
        }
    }

    // --- NOVO MÉTODO PRIVADO ---
    // Converte uma string como "Hidratante de Cereja & Avelã!" para "hidratante-de-cereja-e-avela"
    private String slugify(String text) {
        if (text == null || text.isEmpty()) {
            return "produto-sem-nome";
        }
        // Normaliza para remover acentos
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        // Remove caracteres diacríticos
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noAccents = pattern.matcher(normalized).replaceAll("");
        
        return noAccents
                .toLowerCase()
                .replaceAll("\\s+", "-") // Substitui espaços por hífens
                .replaceAll("[^a-z0-9\\-]", ""); // Remove todos os caracteres que não sejam letras, números ou hífens
    }
}