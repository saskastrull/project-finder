package org.example.projectfinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.exception.ResourceNotFoundException;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.model.entity.Project;
import org.example.projectfinder.repository.KeywordRepository;
import org.example.projectfinder.repository.ProjectRepository;
import org.example.projectfinder.repository.specification.ProjectSpecifications;
import org.example.projectfinder.utility.ProjectMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjects(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        Specification<Project> spec = Specification
                .where(ProjectSpecifications.startDateAfter(startDate))
                .and(ProjectSpecifications.endDateBefore(endDate));

        return projectRepository.findAll(spec).stream()
                .map(ProjectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return ProjectMapper.toDto(project);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project", id);
        }
        projectRepository.deleteById(id);
    }

    public void deleteAllProjects() {
        projectRepository.deleteAll();
    }

    /**
     * Saves all scraped projects from a single website to database.
     * Method is called in ScraperService for each website to be scraped.
     * @param scrapedProjects list of projects scraped from one website
     */
    @Transactional
    public void createScrapedProjects(List<ProjectDto> scrapedProjects) {

        List<String> keywords = keywordRepository.findAll()
                .stream()
                .map(k -> k.getKeyword().toLowerCase())
                .toList();

        for (ProjectDto dto : scrapedProjects) {

            // Skip if URL already exists in database (unique value to avoid duplicates)
            if (projectRepository.existsByUrl(dto.getUrl())) {
                continue;
            }

            // Check if description contains any keyword
            String description = dto.getDescription().toLowerCase();

            boolean matchesKeyword = keywords.stream()
                    .anyMatch(description::contains);

            if (!matchesKeyword) {
                continue;
            }

            Project project = new Project(
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.getDescription(),
                    dto.getLocation(),
                    dto.getDeadline(),
                    dto.getHours(),
                    dto.getOrigin(),
                    dto.getUrl()
            );

            projectRepository.save(project);
        }
    }
}
