package com.example.taxone.repository;

import com.example.taxone.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("""
        SELECT DISTINCT p
        FROM Project p
        LEFT JOIN p.projectMembers pm
        WHERE p.workspace.id = :workspaceId
          AND (p.isPublic = true OR pm.user.id = :userId)
    """)
    List<Project> findVisibleProjects(
            @Param("workspaceId") UUID workspaceId,
            @Param("userId") UUID userId
    );

    List<Project> findAllByWorkspaceId(UUID id);
}
