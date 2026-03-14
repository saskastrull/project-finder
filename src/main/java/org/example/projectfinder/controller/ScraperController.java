package org.example.projectfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.model.dto.KeywordDto;
import org.example.projectfinder.service.ScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scraper")
@RequiredArgsConstructor
public class ScraperController {

    private final ScraperService scraperService;

    // ADMIN ENDPOINT

    @PostMapping("/run")
    public ResponseEntity<Void> runScrapers() {
        scraperService.scrapeAll();
        return ResponseEntity.accepted().build();
    }
}
