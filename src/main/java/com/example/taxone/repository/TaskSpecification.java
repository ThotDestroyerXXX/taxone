package com.example.taxone.repository;

import com.example.taxone.dto.request.TaskFilterRequest;
import com.example.taxone.entity.Label;
import com.example.taxone.entity.Task;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskSpecification {

    public static Specification<Task> withFilters(
            UUID projectId,
            TaskFilterRequest filter
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("project").get("id"), projectId)
            );

            if (filter.getStatus() != null) {
                predicates.add(
                        cb.equal(root.get("status"), filter.getStatus())
                );
            }

            if (filter.getPriority() != null) {
                predicates.add(
                        cb.equal(root.get("priority"), filter.getPriority())
                );
            }

            if (filter.getDueBefore() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("dueDate"),
                                filter.getDueBefore()
                        )
                );
            }

            if (filter.getDueAfter() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("dueDate"),
                                filter.getDueAfter()
                        )
                );
            }

            if (filter.getLabelIds() != null && !filter.getLabelIds().isEmpty()) {
                Join<Task, Label> labelJoin =
                        root.join("labels", JoinType.LEFT);

                predicates.add(
                        labelJoin.get("id").in(filter.getLabelIds())
                );

                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

