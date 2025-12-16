package org.example.projectfinder.repository;

import org.example.projectfinder.model.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
