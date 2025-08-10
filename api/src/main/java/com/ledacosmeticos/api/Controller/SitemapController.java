package com.ledacosmeticos.api.Controller;

import com.ledacosmeticos.api.Sitemap.UrlSet;
import com.ledacosmeticos.api.Service.SitemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SitemapController {

    @Autowired
    private SitemapService sitemapService;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<UrlSet> getSitemap() {
        return ResponseEntity.ok(sitemapService.createSitemap());
    }
}