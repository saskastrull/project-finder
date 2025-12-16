package org.example.projectfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.model.entity.Keyword;
import org.example.projectfinder.model.entity.Project;
import org.example.projectfinder.repository.ProjectRepository;
import org.example.projectfinder.utility.KeywordMapper;
import org.example.projectfinder.utility.ProjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectDto> getAllProjects() {
        try {
            List<Project> projects = projectRepository.findAll();
            return projects.stream()
                    .map(ProjectMapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // denna klass ska kunna posta projekt till databasen enl schema
    // logga detta (hur många uppdrag som matchar vilka keywords och hur många som loggats?)

}
