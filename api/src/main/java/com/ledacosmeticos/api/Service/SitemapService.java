package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.Repository.CategoriaRepository;
import com.ledacosmeticos.api.Repository.ProdutoRepository;
import com.ledacosmeticos.api.Sitemap.Url;
import com.ledacosmeticos.api.Sitemap.UrlSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class SitemapService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private static final String BASE_URL = "https://www.ledacosmeticos.com.br";

    public UrlSet createSitemap() {
        UrlSet urlSet = new UrlSet();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 1. Páginas estáticas
        urlSet.getUrls().add(new Url(BASE_URL + "/", today, "daily", "1.0"));
        urlSet.getUrls().add(new Url(BASE_URL + "/produtos", today, "daily", "0.9"));
        urlSet.getUrls().add(new Url(BASE_URL + "/presentes", today, "monthly", "0.8"));
        urlSet.getUrls().add(new Url(BASE_URL + "/sobre-nos", today, "yearly", "0.5"));

        // 2. Produtos ativos
        produtoRepository.findByAtivoTrue().stream()
                .filter(produto -> Objects.nonNull(produto) && Objects.nonNull(produto.getId()))
                .forEach(produto -> urlSet.getUrls().add(
                        new Url(BASE_URL + "/produtos/" + produto.getId(), today, "weekly", "0.8")
                ));

        // 3. Categorias
        categoriaRepository.findAll().stream()
                .filter(categoria -> Objects.nonNull(categoria) &&
                        Objects.nonNull(categoria.getId()) &&
                        Objects.nonNull(categoria.getTipo()))
                .forEach(categoria -> {
                    String path = categoria.getTipo().toString().equalsIgnoreCase("PRESENTE")
                            ? "/presentes"
                            : "/produtos";
                    urlSet.getUrls().add(new Url(
                            BASE_URL + path + "?categoria=" + categoria.getId(),
                            today, "weekly", "0.7")
                    );
                });

        return urlSet;
    }
}
