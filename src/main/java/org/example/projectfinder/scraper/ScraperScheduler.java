package org.example.projectfinder.scraper;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.service.ScraperService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScraperScheduler {

    private final ScraperService scraperService;

    @Scheduled (cron = "0 0 1 * * ?") // (fixedRate = 100000) for testing
    public void runScrapers() {
        scraperService.scrapeAll();
    }
}
