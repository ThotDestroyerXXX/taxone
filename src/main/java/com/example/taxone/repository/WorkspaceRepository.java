package com.example.taxone.repository;

import com.example.taxone.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    Boolean existsBySlug(String slug);
    List<Workspace> findByOwnerId(UUID id);
    Workspace findByIdAndOwnerId(UUID id, UUID ownerId);

    @Query("""
    SELECT DISTINCT w
    FROM Workspace w
    LEFT JOIN w.workspaceMembers m
    WHERE w.isActive = TRUE AND (w.owner.id = :userId
       OR m.user.id = :userId)
""")
    List<Workspace> findAllByUserId(@Param("userId") UUID userId);

    @Query("""
    SELECT w
    FROM Workspace w
    LEFT JOIN w.workspaceMembers m
    WHERE w.isActive = TRUE AND w.id = :workspaceId
      AND (w.owner.id = :userId OR m.user.id = :userId)
""")
    Optional<Workspace> findByIdAndUserHasAccess(
            @Param("workspaceId") UUID workspaceId,
            @Param("userId") UUID userId
    );

    boolean existsBySlugAndIdNot(String slug, UUID id);

}
