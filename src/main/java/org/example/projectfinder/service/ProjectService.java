package org.example.projectfinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.exception.ResourceNotFoundException;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.model.entity.Project;
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

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjects(
            LocalDate startDate,
            LocalDate endDate,
            Long keywordId) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        Specification<Project> spec = Specification
                .where(ProjectSpecifications.startDateAfter(startDate))
                .and(ProjectSpecifications.endDateBefore(endDate))
                .and(ProjectSpecifications.hasKeyword(keywordId));

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

    // Called for scraping
    public void createProject(LocalDate startDate, LocalDate endDate, String description, String location,
                              LocalDate deadline, String company, int hours, String url) {
        projectRepository.save(new Project(startDate, endDate, description, location,
                deadline, company, hours, url));
        log.info("(wip) project stored with location: {}", location);
    }
}
