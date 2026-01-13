package org.example.projectfinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.model.entity.Project;
import org.example.projectfinder.repository.ProjectRepository;
import org.example.projectfinder.utility.ProjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectDto> getProjects(LocalDate startDate, LocalDate endDate) {
        List<Project> projects;

        if (startDate == null && endDate == null) {
            projects = projectRepository.findAll();

        } else if (startDate != null && endDate == null) {
            projects = projectRepository.findByStartDateAfter(startDate);

        } else if (startDate == null) {
            projects = projectRepository.findByEndDateBefore(endDate);

        } else {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("startDate must be before endDate");
            }
            projects = projectRepository.findByStartDateBetween(startDate, endDate);
        }

        return projects.stream()
                .map(ProjectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id) {

        // best practices för Optional<Project> ?

        // Project project = projectRepository.findById(id); ---> returnerar Optional<Project>
        // return ProjectMapper.toDto(project);

        return null;
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public List<ProjectDto> getProjectsByDate(LocalDate startDate, LocalDate endDate) {
        return null;
    }

    // Metod som används i scrapern
    // Här hade det varit trevligt att ha en logger!
    public void createProject(LocalDate startDate, LocalDate endDate, String description, String location, String url) {
        projectRepository.save(new Project(startDate, endDate, description, location, url));
        log.info("(wip) project stored with location: {}", location);
    }
}
