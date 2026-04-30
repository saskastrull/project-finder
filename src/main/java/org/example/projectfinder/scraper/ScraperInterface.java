package org.example.projectfinder.scraper;

import org.example.projectfinder.model.dto.ProjectDto;

import java.util.List;

public interface ScraperInterface {
    List<ProjectDto> scrape();
}
