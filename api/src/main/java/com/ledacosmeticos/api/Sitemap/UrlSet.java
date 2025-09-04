package com.ledacosmeticos.api.Sitemap;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "urlset")
public class UrlSet {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    private String xmlns = "http://www.sitemaps.org/schemas/sitemap/0.9";

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "url")
    private List<Url> urls = new ArrayList<>();
}
