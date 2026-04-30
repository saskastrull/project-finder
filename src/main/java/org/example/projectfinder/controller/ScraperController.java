package org.example.projectfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.service.ScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraper")
@RequiredArgsConstructor
public class ScraperController {

    private final ScraperService scraperService;

    // ADMIN endpoints
    @PostMapping("/run")
    public ResponseEntity<Void> runScrapers() {
        scraperService.scrapeAll();
        return ResponseEntity.accepted().build();
    }
}
