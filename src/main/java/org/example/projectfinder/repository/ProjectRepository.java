package org.example.projectfinder.repository;

import org.example.projectfinder.model.entity.Keyword;
import org.example.projectfinder.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {
    boolean existsByUrl(String url);
}
