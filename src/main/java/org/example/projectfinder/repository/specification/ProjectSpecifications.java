package org.example.projectfinder.repository.specification;

import jakarta.persistence.criteria.Join;
import org.example.projectfinder.model.entity.Project;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ProjectSpecifications {

    public static Specification<Project> startDateAfter(LocalDate startDate) {
        return (root, query, cb) ->
                startDate == null ? null :
                        cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
    }

    public static Specification<Project> endDateBefore(LocalDate endDate) {
        return (root, query, cb) ->
                endDate == null ? null :
                        cb.lessThanOrEqualTo(root.get("endDate"), endDate);
    }

    public static Specification<Project> hasKeyword(Long keywordId) {
        return (root, query, cb) -> {
            if (keywordId == null) return null;

            query.distinct(true);
            Join<Object, Object> keywords = root.join("keywords");
            return cb.equal(keywords.get("id"), keywordId);
        };
    }
}

