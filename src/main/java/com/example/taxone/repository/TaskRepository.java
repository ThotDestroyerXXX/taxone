package com.example.taxone.repository;

import com.example.taxone.dto.request.TaskFilterRequest;
import com.example.taxone.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    List<Task> findAllByProjectId(UUID projectId);
    @Query("SELECT MAX(CAST(SUBSTRING(t.taskKey, LENGTH(:projectKey) + 2) AS int)) " +
            "FROM Task t WHERE t.project.id = :projectId")
    Integer findMaxTaskNumberByProject(@Param("projectId") UUID projectId, @Param("projectKey") String projectKey);

    // Alternative: Get the last task key directly
    @Query("SELECT t.taskKey FROM Task t WHERE t.project.id = :projectId ORDER BY t.taskKey DESC LIMIT 1")
    Optional<String> findLastTaskKeyByProject(@Param("projectId") UUID projectId);
    default List<Task> findByProjectWithFilters(
            UUID projectId,
            TaskFilterRequest filter
    ) {
        return findAll(
                TaskSpecification.withFilters(projectId, filter)
        );
    }

    List<Task> findByAssignees_Id(UUID userId);
    List<Task> findByReporterId(UUID reporterId);
}
