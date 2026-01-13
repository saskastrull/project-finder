package org.example.projectfinder.repository;

import org.example.projectfinder.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStartDateAfter(LocalDate startDate);
    List<Project> findByEndDateBefore(LocalDate endDate);
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
