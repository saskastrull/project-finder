package org.example.projectfinder.scraper.website;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.scraper.ScraperInterface;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Scraper implementation for the Verama website.
 *
 * <p>This scraper parses a JSON GET response using Jackson.</p>
 */
@Slf4j
@Component
public class VeramaScraper implements ScraperInterface {

    // Found in DevTools -> Fetch/XHR
    // Adjust "size" depending on how many projects to get per page, currently size=500 (only one page needed to collect all jobs on the site)
    private static final String REQUEST_URL = "https://app.verama.com/api/public/job-requests?page=%d&size=500&query=&dedicated=false&favouritesOnly=false&recommendedOnly=false&sort=firstDayOfApplications,DESC";

    // Used for time formatting
    private static final double FULL_TIME_HOURS = 40.0;

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Scrapes the Verama site of all projects.
     *
     * <p>Fetches a JSON GET response of adjustable page size
     * containing job listings and parses the JSON nodes to
     * {@link ProjectDto}.</p>
     *
     * @return list of scraped projects mapped to {@link ProjectDto}
     */
    @Override
    public List<ProjectDto> scrape() {
        List<ProjectDto> scrapedProjects = new ArrayList<>();

        int page = 0;
        int totalPages = Integer.MAX_VALUE; // Assume large amount of pages

        while (page < totalPages) {
            try {
                String url = String.format(
                        REQUEST_URL,
                        page
                );

                log.debug("[Verama] Fetching page {}", page);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .header("User-Agent", "Mozilla/5.0")
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    log.error("[Verama] HTTP error: {}", response.statusCode());
                    break;
                }

                JsonNode root = mapper.readTree(response.body());
                JsonNode contentNode = root.path("content");

                // Stop if no content
                if (!contentNode.isArray() || contentNode.isEmpty()) {
                    log.info("[Verama] No more content, stopping");
                    break;
                }

                for (JsonNode job : contentNode) {
                    ProjectDto dto = mapToDto(job);
                    scrapedProjects.add(dto);
                }

                page++;

            } catch (IOException | InterruptedException e) {
                log.error("[Verama] Scraping failed", e);
                break;
            }
        }

        log.info("[Verama] Jobs found: {}", scrapedProjects.size());
        return scrapedProjects;
    }

    /**
     * Parses a JsonNode and maps relevant fields to a project.
     * @param node JsonNode to parse
     * @return project as ProjectDto
     */
    private ProjectDto mapToDto(JsonNode node) {
        ProjectDto dto = new ProjectDto();

        // Use title as description
        if (node.hasNonNull("title")) {
            dto.setDescription(node.get("title").asText());
        }

        // Pick first location if it exists
        if (node.hasNonNull("locations") && node.get("locations").isArray() && !node.get("locations").isEmpty()) {
            JsonNode firstLocation = node.get("locations").get(0);
            if (firstLocation.hasNonNull("name")) {
                dto.setLocation(firstLocation.get("name").asText());
            }
        }

        // Get startDate and endDate
        if (node.hasNonNull("startDate")) {
            dto.setStartDate(parseLocalDate(node.get("startDate").asText()));
        }
        if (node.hasNonNull("endDate")) {
            dto.setEndDate(parseLocalDate(node.get("endDate").asText()));
        }

        // Get expiration (lastDayOfApplications)
        if (node.hasNonNull("lastDayOfApplications")) {
            dto.setExpiration(parseLocalDate(node.get("lastDayOfApplications").asText()));
        }

        // Get hoursPerWeek and transform to percentage
        if (node.hasNonNull("hoursPerWeek")) {
            dto.setHours((int) getHoursAsPercentage(node.get("hoursPerWeek").asDouble()));
        } else {
            dto.setHours(0);
        }

        // Generate URL, remove "JR-" from systemId since the URLs lack it
        if (node.hasNonNull("systemId")) {
            String systemId = node.get("systemId").asText();

            if (systemId.startsWith("JR-")) {
                systemId = systemId.substring(3);
            }

            dto.setUrl("https://app.verama.com/job-requests/" + systemId);
        }

        return dto;
    }

    /**
     * Calculates the workload percentage from hours per week (full-time baseline).
     * @param hours per week
     * @return percentage per week
     */
    private double getHoursAsPercentage(double hours) {
        return (hours / FULL_TIME_HOURS) * 100.0;
    }

    /**
     * Parses String value collected from project node.
     * @param value from JsonNode containing date
     * @return correctly formatted LocalDate for a ProjectDto
     */
    private LocalDate parseLocalDate(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            return LocalDate.parse(value.substring(0, 10)); // Only keep yyyy-mm-dd
        } catch (Exception e) {
            log.warn("[Verama] Failed to parse date: {}", value, e);
            return null;
        }
    }
}
