package org.example.projectfinder.repository.specification;

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
}

