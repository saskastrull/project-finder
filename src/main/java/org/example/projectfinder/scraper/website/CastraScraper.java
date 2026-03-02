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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Scraper implementation for the Castra website.
 *
 * <p>This scraper retrieves job listings via a GraphQL POST request to a RedwoodJS backend
 * and parses the response using Jackson.</p>
 */
@Slf4j
@Component
public class CastraScraper implements ScraperInterface {

    // Found in DevTools -> Fetch/XHR
    private static final String REQUEST_URL = "https://partnernatverk.castra.se/.redwood/functions/graphql";

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Scrapes the Castra site of all projects.
     *
     * <p>Fetches a JSON POST response containing job listings
     * and parses the JSON nodes to {@link ProjectDto}.</p>
     *
     * @return list of scraped projects mapped to {@link ProjectDto}
     */
    @Override
    public List<ProjectDto> scrape() {
        List<ProjectDto> scrapedProjects = new ArrayList<>();

        int page = 1;

        while (true) {
            try {
                String body = buildRequestBody(page);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(REQUEST_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    log.error("[Castra] HTTP error: {}", response.statusCode());
                    break;
                }

                JsonNode root = mapper.readTree(response.body());
                JsonNode requestsNode = root.path("data").path("requestsPage").path("requests");

                // If there's no more pages
                if (!requestsNode.isArray() || requestsNode.isEmpty()) {
                    break;
                }

                for (JsonNode node : requestsNode) {
                    ProjectDto dto = mapToDto(node);
                    scrapedProjects.add(dto);
                }

                page++;

            } catch (IOException | InterruptedException e) {
                log.error("[Castra] Scraping failed", e);
                break;
            }
        }

        log.info("[Castra] Jobs found: {}", scrapedProjects.size());
        return scrapedProjects;
    }

    /**
     * Builds request body based on GraphQL query found in DevTools.
     * @param page used as variable in GraphQL query
     * @return JSON response containing job listings
     */
    private String buildRequestBody(int page) {
        return """
        {
          "operationName":"RequestsPageQuery",
          "variables":{"page":%d},
          "query":"query RequestsPageQuery($page: Int) { requestsPage(page: $page) { requests { id requestId title description announcedDate deadlineDate startDate endDate locationDisplayName locationCity allowRemote marketURL price announcerCompanyName __typename } count __typename } }"
        }
        """.formatted(page);
    }

    /**
     * Maps node of JSON into DTO.
     * @param node to be converted into DTO
     * @return DTO to save in scraped projects
     */
    private ProjectDto mapToDto(JsonNode node) {
        ProjectDto dto = new ProjectDto();

        if (node.hasNonNull("title")) {
            dto.setDescription(node.get("title").asText());
        }

        if (node.hasNonNull("locationDisplayName")) {
            dto.setLocation(node.get("locationDisplayName").asText());
        } else if (node.hasNonNull("locationCity")) {
            dto.setLocation(node.get("locationCity").asText());
        }

        if (node.hasNonNull("startDate")) {
            dto.setStartDate(parseDate(node.get("startDate")));
        }

        if (node.hasNonNull("endDate")) {
            dto.setEndDate(parseDate(node.get("endDate")));
        }

        if (node.hasNonNull("deadlineDate")) {
            dto.setExpiration(parseDate(node.get("deadlineDate")));
        }

        if (node.hasNonNull("marketURL")) {
            dto.setUrl(node.get("marketURL").asText());
        }
        return dto;
    }

    /**
     * Formats Castra's full ISO dates into LocalDates.
     * @param dateNode ISO response example: "deadlineDate": "2026-02-19T00:00:00.000Z"
     * @return LocalDate to be stored in DTO
     */
    private LocalDate parseDate(JsonNode dateNode) {
        try {
            if (dateNode == null || dateNode.isNull()) return null;

            String value = dateNode.asText();
            if (value.isBlank()) return null;

            return OffsetDateTime.parse(value).toLocalDate();

        } catch (Exception e) {
            log.warn("[Castra] Failed to parse date: {}", dateNode, e);
            return null;
        }
    }
}
