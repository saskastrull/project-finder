package org.example.projectfinder.scraper.website;

import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.scraper.ScraperInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ASocietyGroupScraper implements ScraperInterface {

    // Pages look like: https://www.asocietygroup.com/sv/uppdrag?page=13
    private static final String LISTING_URL = "https://www.asocietygroup.com/sv/uppdrag";

    private static final String FETCH_URL = "https://www.asocietygroup.com/sv/uppdrag?_rsc=1dm2w";

    /**
     * Scrape the site of all projects.
     * @return list of all scraped projects from this site
     */
    @Override
    public List<ProjectDto> scrape() {
        List<ProjectDto> scrapedProjects = new ArrayList<>();

        // Finns i DevTools Network Doc "uppdrag"

        int page = 0;

        while (page < 3) {
            try {
                String body = Jsoup.connect(FETCH_URL)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .execute()
                        .body();

                int start = body.indexOf("{\"requisition");
                int end = body.lastIndexOf("}");

                String escapedJson = body.substring(start, end + 1);

                System.out.println(escapedJson);

                page++;

            } catch (IOException e) {
                break;
            }
        }

        log.info("[ASocietyGroup] Jobs found: {}", scrapedProjects.size());

        return scrapedProjects;
    }
}
