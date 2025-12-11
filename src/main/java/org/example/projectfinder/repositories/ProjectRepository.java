package org.example.projectfinder.repositories;

import org.example.projectfinder.models.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
