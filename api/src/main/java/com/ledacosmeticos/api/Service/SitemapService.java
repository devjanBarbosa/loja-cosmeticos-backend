package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.Model.Categoria;
import com.ledacosmeticos.api.Model.Produto;
import com.ledacosmeticos.api.Repository.CategoriaRepository;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import com.ledacosmeticos.api.Sitemap.Url;
import com.ledacosmeticos.api.Sitemap.UrlSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects; // Importe a classe Objects

@Service
public class SitemapService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private final String BASE_URL = "https://www.ledacosmeticos.com.br"; 

    public UrlSet createSitemap() {
        UrlSet urlSet = new UrlSet();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 1. Adicionar páginas estáticas (sem alteração)
        urlSet.getUrl().add(new Url(BASE_URL + "/", today, "daily", "1.0"));
        urlSet.getUrl().add(new Url(BASE_URL + "/produtos", today, "daily", "0.9"));
        urlSet.getUrl().add(new Url(BASE_URL + "/presentes", today, "monthly", "0.8"));
        urlSet.getUrl().add(new Url(BASE_URL + "/sobre-nos", today, "yearly", "0.5"));

        // 2. Adicionar todas as páginas de produtos
        for (Produto produto : produtoRepository.findByAtivoTrue()) {
            // Garante que o produto e o ID não são nulos
            if (produto != null && produto.getId() != null) {
                urlSet.getUrl().add(new Url(BASE_URL + "/produtos/" + produto.getId(), today, "weekly", "0.8"));
            }
        }

        // 3. Adicionar páginas de categorias
        for (Categoria categoria : categoriaRepository.findAll()) {
            // ====================================================================
            // >>> A CORREÇÃO PRINCIPAL ESTÁ AQUI <<<
            // Adicionamos uma verificação para garantir que o tipo não é nulo
            // ====================================================================
            if (categoria != null && categoria.getId() != null && categoria.getTipo() != null) {
                String path = categoria.getTipo().toString().equalsIgnoreCase("PRESENTE") ? "/presentes" : "/produtos";
                urlSet.getUrl().add(new Url(BASE_URL + path + "?categoria=" + categoria.getId(), today, "weekly", "0.7"));
            }
        }
        
        return urlSet;
    }
}