package org.example.projectfinder.scraper.website;

import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.scraper.ScraperInterface;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProfinderScraper implements ScraperInterface {

    private static final String LISTING_URL = "https://www.profinder.se/lediga-uppdrag";

    /**
     * Scrapes the Profinder site of all projects.
     *
     *
     *
     * @return list of scraped projects mapped to {@link ProjectDto}
     */
    @Override
    public List<ProjectDto> scrape() {
        List<ProjectDto> scrapedProjects = new ArrayList<>();

        log.info("[Profinder] Jobs found: {}", scrapedProjects.size());

        return List.of();
    }
}
