package org.example.projectfinder.utility;

import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.model.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public static ProjectDto toDto(Project project) {
        return new ProjectDto(
                project.getProjectId(),
                project.getStartDate(),
                project.getEndDate(),
                project.getDescription(),
                project.getLocation(),
                project.getDeadline(),
                project.getHours(),
                project.getUrl()
        );
    }

    public static Project toEntity(ProjectDto projectDto) {
        return new Project(
                projectDto.getStartDate(),
                projectDto.getEndDate(),
                projectDto.getDescription(),
                projectDto.getLocation(),
                projectDto.getExpiration(),
                projectDto.getHours(),
                projectDto.getUrl()
        );
    }
}