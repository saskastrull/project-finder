package org.example.projectfinder.scraper;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.service.ProjectService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebScraper {

    private List<String> sites;
    private final ProjectService projectService;

    @Scheduled(fixedRate = 5000)
    public void scrapeSites() {

        LocalDate startDate = LocalDate.of(1997, 4, 27);
        LocalDate endDate = LocalDate.of(2027, 9, 3);
        String description = "beskrivning";
        String location = "plats";
        LocalDate expiration = LocalDate.of(2020, 9, 3);
        String company = "fujitsu";
        int hours = 100;
        String url = "länk";

        projectService.createProject(startDate, endDate, description, location, expiration, company, hours, url);
    }
}
