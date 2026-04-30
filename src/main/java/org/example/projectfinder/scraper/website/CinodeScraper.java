package org.example.projectfinder.scraper.website;

import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.scraper.ScraperInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Scrapes the Cinode website document and parses the data to a
 * list of {@link ProjectDto}.
 * <p>
 * The Cinode listings are available directly in the HTML, allowing them
 * to be parsed without executing client-side JavaScript. Pagination is also handled
 * via a cursor-based "load more" mechanism exposed in the document.
 */
@Slf4j
@Component
public class CinodeScraper implements ScraperInterface {

    // Name tag to be stored in each ProjectDto
    private static final String SITE_NAME = "Cinode";

    // Doc URL
    private static final String BASE_URL = "https://cinode.com";
    private static final String LISTING_URL = BASE_URL + "/market/requests";

    /**
     * Fetches the website document and then calls helper methods to
     * extract the jobs and map them to {@link ProjectDto}.
     *
     * @return list of scraped projects mapped to {@link ProjectDto}
     */
    @Override
    public List<ProjectDto> scrape() {
        List<ProjectDto> projects = new ArrayList<>();
        Set<String> seenCursors = new HashSet<>();

        try {
            String url = LISTING_URL;

            while (url != null) {
                log.info("[Cinode] Fetching: {}", url);

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Connection", "keep-alive")
                        .header("Upgrade-Insecure-Requests", "1")
                        .referrer("https://www.google.com/")
                        .timeout(60_000)
                        .get();

                List<ProjectDto> pageProjects = parseRows(doc);
                projects.addAll(pageProjects);

                String cursor = extractCursor(doc);

                if (cursor == null || seenCursors.contains(cursor)) {
                    log.info("[Cinode] No more pages");
                    break;
                }

                seenCursors.add(cursor);
                url = LISTING_URL + "?nextCursor=" + cursor;

                Thread.sleep(500);
            }

        } catch (Exception e) {
            log.error("[Cinode] Scraping failed", e);
        }

        log.info("[Cinode] Total jobs found: {}", projects.size());
        return projects;
    }

    /**
     * Extracts all listing rows from the document and maps them to {@link ProjectDto}.
     */
    private List<ProjectDto> parseRows(Document doc) {
        List<ProjectDto> list = new ArrayList<>();

        Elements rows = doc.select("[e2e-market-request-row]");

        for (Element row : rows) {
            try {
                ProjectDto dto = mapRow(row);
                if (dto != null) {
                    list.add(dto);
                }
            } catch (Exception e) {
                log.warn("[Cinode] Failed to parse row", e);
            }
        }

        return list;
    }

    /**
     * Maps a single listing element to a {@link ProjectDto} by extracting any relevant fields.
     */
    private ProjectDto mapRow(Element row) {
        ProjectDto dto = new ProjectDto();

        // Title/URL
        Element titleEl = row.selectFirst("[e2e-market-request-link]");
        if (titleEl == null) return null;

        dto.setDescription(titleEl.text().trim());
        dto.setUrl(BASE_URL + titleEl.attr("href"));

        // Date and location
        Elements detailItems = row.select(".list__details .focus__item");

        for (Element item : detailItems) {
            Element use = item.selectFirst("use");
            Element p = item.selectFirst("p");

            if (use == null || p == null) continue;

            String icon = use.attr("href");
            if (icon.isBlank()) {
                icon = use.attr("xlink:href");
            }

            String text = p.text().trim();

            // Parse dates formatted like "1 Oct, 2026 to 30 Sep, 2028"
            if (icon.contains("calendar")) {
                if (text.equalsIgnoreCase("No dates set")) {
                    continue;
                }

                if (text.contains(" to ")) {
                    String[] parts = text.split(" to ");
                    dto.setStartDate(parseDate(parts[0]));
                    dto.setEndDate(parseDate(parts[1]));
                } else if (text.startsWith("From")) {
                    String cleaned = text
                            .replace("From ", "")
                            .replace(" without end date", "")
                            .trim();
                    dto.setStartDate(parseDate(cleaned));
                }
            }

            // Location
            if (icon.contains("map-pin")) {
                Element locationLink = item.selectFirst("a[href*='/city/']");
                if (locationLink != null) {
                    dto.setLocation(locationLink.text().trim());
                }
            }
        }

        // Deadline
        dto.setDeadline(extractDeadline(row));

        dto.setOrigin(SITE_NAME);

        return dto;
    }

    /**
     * Extracts and parses the deadline date from the listing footer metadata.
     */
    private LocalDate extractDeadline(Element row) {
        Elements metaItems = row.select(".requests-list__card-meta-item");

        for (Element item : metaItems) {
            String text = item.text().replace("\n", " ").trim();

            if (text.toLowerCase().startsWith("deadline")) {
                String datePart = text.replace("Deadline", "").trim();
                return parseDate(datePart);
            }
        }

        return null;
    }

    /**
     * Retrieves the pagination cursor from the "load more" button, if present.
     */
    private String extractCursor(Document doc) {
        Element btn = doc.selectFirst("#load-more-button");

        if (btn == null) return null;

        String cursor = btn.attr("data-next-cursor");
        return cursor.isBlank() ? null : cursor;
    }

    /**
     * Parses a date string into LocalDate and returns null if parsing fails.
     */
    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;

        raw = raw.trim();

        List<DateTimeFormatter> formats = List.of(
                DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
        );

        for (DateTimeFormatter formatter : formats) {
            try {
                return LocalDate.parse(raw, formatter);
            } catch (Exception ignored) {}
        }

        log.warn("[Cinode] Failed to parse date: {}", raw);
        return null;
    }
}