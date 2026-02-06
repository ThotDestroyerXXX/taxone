package com.example.taxone.repository;

import com.example.taxone.entity.Project;
import com.example.taxone.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    Optional<ProjectMember> findByUserIdAndProjectId(UUID userId, UUID projectId);

    UUID project(Project project);

    List<ProjectMember> findAllByProjectId(UUID projectId);

    Optional<ProjectMember> findByIdAndProjectId(UUID projectId, UUID projectMemberId);
}
