package org.example.projectfinder.scraper.website;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.scraper.ScraperInterface;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class ASocietyGroupScraper implements ScraperInterface {

    // Doc URL
    private static final String LISTING_URL = "https://www.asocietygroup.com/sv/uppdrag";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Scrapes the ASocietyGroup site of all projects.
     *
     *
     *
     * @return list of scraped projects mapped to {@link ProjectDto}
     */
    @Override
    public List<ProjectDto> scrape() {

        List<ProjectDto> projects = new ArrayList<>();

        return projects;
    }
}