package org.example.projectfinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.scraper.ScraperInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScraperService {

    private final ProjectService projectService;
    private final List<ScraperInterface> scrapers;

    public void scrapeAll() {

        // Clear database of old projects before storing new ones
        projectService.deleteAllProjects();

        for (ScraperInterface scraper : scrapers) {
            try {
                List<ProjectDto> scraped = scraper.scrape();
                projectService.createScrapedProjects(scraped);
            } catch (Exception e) {
                log.error("[ScraperService] Scraper {} failed", scraper.getClass().getSimpleName(), e);
            }
        }
    }
}
