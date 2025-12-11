package org.example.projectfinder.repositories;

import org.example.projectfinder.models.entities.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
