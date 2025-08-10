package com.ledacosmeticos.api.Sitemap;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Url {
    @JacksonXmlProperty(localName = "loc")
    private String loc; // URL da página

    @JacksonXmlProperty(localName = "lastmod")
    private String lastmod; // Data da última modificação

    @JacksonXmlProperty(localName = "changefreq")
    private String changefreq; // Frequência de mudança (ex: weekly)

    @JacksonXmlProperty(localName = "priority")
    private String priority; // Prioridade (de 0.0 a 1.0)
}