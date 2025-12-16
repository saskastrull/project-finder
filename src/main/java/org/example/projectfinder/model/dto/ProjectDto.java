package org.example.projectfinder.model.dto;

import lombok.Getter;
import org.example.projectfinder.model.entity.Keyword;

import java.time.LocalDate;
import java.util.Set;

public class ProjectDto {

    @Getter
    private LocalDate startDate;
    @Getter
    private LocalDate endDate;
    @Getter
    private String description;
    @Getter
    private String location;
    @Getter
    private String url;
    @Getter
    private Set<Keyword> keywords;

    public ProjectDto() {}

    public ProjectDto(LocalDate startDate, LocalDate endDate, String description, String location, String url, Set<Keyword> keywords) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.location = location;
        this.url = url;
        this.keywords = keywords;
    }
}
