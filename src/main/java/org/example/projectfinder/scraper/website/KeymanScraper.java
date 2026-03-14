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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Scrapes the Keyman website document and parses the data to a list of
 * {@link ProjectDto}.
 * <p>
 * Extracts the links to individual job/project pages from the main doc
 * and parses the information from HTML tables to DTOs. Pagination works
 * by finding the .e-load-more-anchor in the document until it's not
 * available anymore (which means there are no more pages to load).
 */
@Slf4j
@Component
public class KeymanScraper implements ScraperInterface {

    // Name tag to be stored in each ProjectDto
    private static final String SITE_NAME = "Keyman";

    // Doc URL
    private static final String LISTING_URL = "https://www.keyman.se/sv/uppdrag/";

    /**
     * Starts from the main listing page and follows page links
     * (.e-load-more-anchor) until no more pages are available.
     * For each job link found, the job's detail page is fetched
     * and parsed to {@link ProjectDto} with additional data.
     *
     * @return list of scraped projects mapped to {@link ProjectDto}
     */
    @Override
    public List<ProjectDto> scrape() {
        List<ProjectDto> scrapedProjects = new ArrayList<>();

        String nextPageUrl = LISTING_URL;

        while (nextPageUrl != null) {
            try {
                Document doc = Jsoup.connect(nextPageUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10_000)
                        .get();

                // Select all <a> elements that start with this URL
                Elements jobElements = doc.select("a[href^=https://www.keyman.se/sv/]");

                // Inspect each <a> anchor
                for (Element jobEl : jobElements) {

                    // Get the full URL
                    String detailUrl = jobEl.attr("abs:href");
                    if (detailUrl.isBlank()) continue;

                    // Use REGEX to force URL structure
                    if (!detailUrl.matches("^https://www\\.keyman\\.se/sv/[^/]+/[^/]+/$"))
                        continue;

                    // Block incorrect URLs of page
                    if (detailUrl.contains("/sv/tag/") ||
                            detailUrl.contains("/sv/category/") ||
                            detailUrl.contains("/sv/account/")) {
                        continue;
                    }

                    String title = jobEl.text().trim();
                    if (title.isBlank()) continue;

                    // Set title, site and URL found on main page
                    ProjectDto dto = new ProjectDto();
                    dto.setDescription(title);
                    dto.setUrl(detailUrl);
                    dto.setOrigin(SITE_NAME);

                    extractDetails(dto);

                    scrapedProjects.add(dto);
                }

                // Find next page if any
                Element loadMore = doc.selectFirst(".e-load-more-anchor");

                if (loadMore != null) {
                    nextPageUrl = loadMore.attr("data-next-page");

                    if (nextPageUrl.isBlank()) {
                        nextPageUrl = null;
                    }
                } else {
                    nextPageUrl = null;
                }

            } catch (IOException e) {
                nextPageUrl = null;
            }
        }

        log.info("[Keyman] Jobs found: {}", scrapedProjects.size());
        return scrapedProjects;
    }

    /**
     * Parses String value into LocalDate.
     */
    private LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses String value containing work hours into int.
     */
    private int parseHours(String text) {
        try {
            return Integer.parseInt(text.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Handles cases where there's text in the date section, for example:
     * 2026-02-16 (Offerter kommer att behandlas löpande)
     */
    private String extractDateFromText(String text) {
        if (text == null) return null;

        String[] parts = text.split(" ");
        return parts.length > 0 ? parts[0] : text;
    }

    /**
     * Fetches the detail page of a project and extracts structured
     * metadata from an HTML table. If no recognized table is found,
     * the method simply returns. Any unrecognized labels  of the
     * table are ignored.
     *
     * @param dto containing the URL for the job page
     */
    private void extractDetails(ProjectDto dto) {
        try {
            Document detailDoc = Jsoup.connect(dto.getUrl())
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            // Details are stored in a table
            Element table = detailDoc.selectFirst("table");
            if (table == null) {
                return;
            }

            Elements rows = table.select("tr");

            for (Element row : rows) {
                Elements cols = row.select("td");

                if (cols.size() != 2) continue;

                String label = cols.get(0).text().trim();
                String value = cols.get(1).text().trim();

                switch (label) {
                    case "Startdatum":
                        dto.setStartDate(parseDate(value));
                        break;

                    case "Slutdatum":
                        dto.setEndDate(parseDate(value));
                        break;

                    case "Omfattning":
                        dto.setHours(parseHours(value));
                        break;

                    case "Ort":
                        dto.setLocation(value);
                        break;

                    case "Sista svarsdatum":
                        dto.setExpiration(parseDate(extractDateFromText(value)));
                        break;

                    default:
                        break;
                }
            }

        } catch (Exception ex) {
            log.warn("[Keyman] Failed to extract details for: {}", dto.getUrl());
        }
    }
}
