package com.example.taxone.repository;

import com.example.taxone.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {
    boolean existsByUserIdAndWorkspaceId(UUID userId, UUID workspaceId);
    List<WorkspaceMember> findAllByWorkspaceId(UUID workspaceId);
    Optional<WorkspaceMember> findByUserIdAndWorkspaceId(UUID userId, UUID workspaceId);
    Optional<WorkspaceMember> findByIdAndWorkspaceId(UUID workspaceMemberId, UUID workspaceUUID);
}
