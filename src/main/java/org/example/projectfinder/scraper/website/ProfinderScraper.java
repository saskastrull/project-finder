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

/**
 * Scrapes the Profinder XML file of job listings and parses the data to a
 * list of {@link ProjectDto}.
 * <p>
 * Retrieves an XML sitemap containing job listings, then extracts the job URLs
 * and job titles from the individual doc elements. No pagination logic is needed
 * since all jobs are listed in the sitemap.
 */
@Slf4j
@Component
public class ProfinderScraper implements ScraperInterface {

    // Name tag to be stored in each ProjectDto
    private static final String SITE_NAME = "Profinder";

    // XML sitemap
    private static final String LISTING_URL = "https://www.profinder.se/blog-posts-sitemap.xml";

    /**
     * Fetches the XML file containing job listings. For each job link found,
     * the job's URL is parsed and formatted into a job description.
     *
     * @return list of scraped projects mapped to {@link ProjectDto}
     */
    @Override
    public List<ProjectDto> scrape() {

        List<ProjectDto> scrapedProjects = new ArrayList<>();

        try {
            Document doc = fetch(LISTING_URL);
            Elements urls = doc.select("loc");

            for (Element urlElement : urls) {

                String url = urlElement.text();

                // Make sure only jobs are parsed
                if (!url.contains("/post/")) {
                    continue;
                }

                ProjectDto dto = new ProjectDto();
                dto.setUrl(url);
                dto.setDescription(extractTitleFromUrl(url));
                dto.setOrigin(SITE_NAME);

                scrapedProjects.add(dto);
            }

        } catch (Exception e) {
            log.error("[Profinder] Scraping failed", e);
        }

        log.info("[Profinder] Jobs found: {}", scrapedProjects.size());

        return scrapedProjects;
    }

    /**
     * Fetches and parses an HTML or XML document.
     */
    private Document fetch(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .timeout(15000)
                .header("Accept-Language", "sv-SE,sv;q=0.9,en;q=0.8")
                .get();
    }

    /**
     * Extracts and formats the title based on a job URL.
     */
    private String extractTitleFromUrl(String url) {

        String slug = url.substring(url.lastIndexOf("/post/") + 6);

        // Remove the job id
        slug = slug.replaceAll("-id-\\d+$", "");

        // Convert all hyphens to spaces
        slug = slug.replace("-", " ");

        return capitalize(slug);
    }

    /**
     * Capitalizes every word of the title.
     */
    private String capitalize(String title) {

        String[] words = title.trim().split("\\s+");

        StringBuilder capitalizedTitle = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize first letter and keep the rest untouched
                capitalizedTitle.append(
                        Character.toUpperCase(word.charAt(0))
                ).append(word.substring(1).toLowerCase()).append(" ");
            }
        }

        return capitalizedTitle.toString().trim(); // Remove trailing space
    }
}
