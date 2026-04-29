package org.example.projectfinder.scraper.website;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.scraper.ScraperInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

    /**
     * Scrapes the ASocietyGroup website document and parses the data to a
     * list of {@link ProjectDto}.
     * <p>
     * The ASocietyGroup website hydrates the document using React and all
     * jobs/projects can be parsed from the document with no pagination logic
     * needed.
     */
    @Slf4j
    @Component
    public class ASocietyGroupScraper implements ScraperInterface {

        // Name tag to be stored in each ProjectDto
        private static final String SITE_NAME = "ASocietyGroup";

        // Doc URL
        private static final String LISTING_URL = "https://www.asocietygroup.com/sv/uppdrag";

        private static final ObjectMapper MAPPER = new ObjectMapper();
        private static final DateTimeFormatter DATE_FORMAT =
                DateTimeFormatter.ISO_LOCAL_DATE;

        /**
         * Fetches the website document and then calls helper methods to
         * extract escaped JSON and then map the data to {@link ProjectDto}.
         *
         * @return list of scraped projects mapped to {@link ProjectDto}
         */
        @Override
        public List<ProjectDto> scrape() {

            List<ProjectDto> projects = new ArrayList<>();

            try {
                Document doc = Jsoup.connect(LISTING_URL)
                        .userAgent("Mozilla/5.0")
                        .timeout(15000)
                        .get();

                String html = doc.html();

                List<JsonNode> jobs = extractAssignments(html);

                for (JsonNode node : jobs) {
                    ProjectDto dto = mapToDto(node);
                    if (dto != null) {
                        dto.setOrigin(SITE_NAME);
                        projects.add(dto);
                    }
                }

                log.info("[ASocietyGroup] Jobs found: {}", projects.size());

            } catch (Exception e) {
                log.error("[ASocietyGroup] Scraping failed", e);
            }

            return projects;
        }

        /**
         * Iterates through all self.__next_f.push(...) calls in the document
         * and extracts the one containing allAssignments.
         */
        private List<JsonNode> extractAssignments(String html) {

            int index = 0;

            while ((index = html.indexOf("self.__next_f.push(", index)) != -1) {

                int start = html.indexOf('[', index);
                int end = findClosingBracket(html, start);

                if (start == -1 || end == -1) break;

                String pushArrayJson = html.substring(start, end + 1);

                try {
                    JsonNode outerArray = MAPPER.readTree(pushArrayJson);

                    if (outerArray.size() < 2 || !outerArray.get(1).isTextual()) {
                        index = end;
                        continue;
                    }

                    String payload = outerArray.get(1).asText();

                    List<JsonNode> assignments = parsePayload(payload);

                    if (!assignments.isEmpty()) {
                        return assignments;
                    }

                } catch (Exception ignored) { }

                index = end;
            }

            return Collections.emptyList();
        }

        /**
         * Finds matching closing bracket for nested JSON arrays.
         */
        private int findClosingBracket(String text, int openIndex) {

            int depth = 0;

            for (int i = openIndex; i < text.length(); i++) {
                char c = text.charAt(i);

                if (c == '[') depth++;
                if (c == ']') depth--;

                if (depth == 0) return i;
            }

            return -1;
        }

        /**
         * Parses the inner Next.js payload (double-deserialization).
         */
        private List<JsonNode> parsePayload(String payload) throws Exception {

            int colonIndex = payload.indexOf(':');
            if (colonIndex == -1) return Collections.emptyList();

            String innerJson = payload.substring(colonIndex + 1);

            // Replace React references like "$35"
            innerJson = innerJson.replaceAll("\"\\$\\d+\"", "null");

            JsonNode innerArray = MAPPER.readTree(innerJson);

            return findAssignments(innerArray);
        }

        /**
         * Recursively searches for "allAssignments" in nested JSON.
         */
        private List<JsonNode> findAssignments(JsonNode node) {

            if (node == null) return Collections.emptyList();

            if (node.has("allAssignments")) {
                List<JsonNode> result = new ArrayList<>();
                node.get("allAssignments").forEach(result::add);
                return result;
            }

            if (node.isObject()) {
                Iterator<JsonNode> fields = node.elements();
                while (fields.hasNext()) {
                    List<JsonNode> found = findAssignments(fields.next());
                    if (!found.isEmpty()) return found;
                }
            }

            if (node.isArray()) {
                for (JsonNode child : node) {
                    List<JsonNode> found = findAssignments(child);
                    if (!found.isEmpty()) return found;
                }
            }

            return Collections.emptyList();
        }

        /**
         * Maps a JSON node to {@link ProjectDto}.
         */
        private ProjectDto mapToDto(JsonNode node) {

            if (node == null) return null;

            ProjectDto dto = new ProjectDto();

            dto.setDescription(
                    decodeHtml(node.path("requisition_name").asText(null))
            );

            dto.setStartDate(parseDate(node.path("requisition_startdate").asText(null)));

            dto.setEndDate(parseDate(node.path("requisition_enddate").asText(null)));

            dto.setDeadline(parseDate(node.path("requisition_offerduedate").asText(null)));

            dto.setLocation(node.path("requisition_locationid").asText(null));

            if (node.hasNonNull("requisition_worktimepercentage")) {
                dto.setHours(node.get("requisition_worktimepercentage").asInt());
            }

            // Create the URL (example: */projektledare-it-med-kravanalyskompetens-15413)
            String title = node.path("requisition_name").asText(null);
            String id = node.path("abstract_id").asText(null);

            if (title != null && id != null) {
                String slug = createTitleUrl(title);
                String url = "https://www.asocietygroup.com/sv/uppdrag/"
                        + slug + "-" + id;

                dto.setUrl(url);
            }

            return dto;
        }

        /**
         * Normalizes a job title to be used in the {@link ProjectDto} URL.
         */
        private String createTitleUrl(String input) {

            if (input == null) return null;

            // Remove special characters like å ä ö
            String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

            // Make lowercase
            normalized = normalized.toLowerCase(Locale.ROOT);

            // Replace all non alphanumerics with dash
            normalized = normalized.replaceAll("[^a-z0-9]", "-");

            // If multiple dashes, turn into one
            normalized = normalized.replaceAll("-{2,}", "-");

            // Trim dashes
            return normalized.replaceAll("^-|-$", "");
        }

        /**
         * Parses a date into a LocalDate to be stored in {@link ProjectDto}.
         */
        private LocalDate parseDate(String value) {
            try {
                if (value == null || value.isBlank()) return null;
                return LocalDate.parse(value, DATE_FORMAT);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Unescapes a String value.
         */
        private String decodeHtml(String value) {
            if (value == null) return null;
            return org.jsoup.parser.Parser.unescapeEntities(value, false);
        }
    }