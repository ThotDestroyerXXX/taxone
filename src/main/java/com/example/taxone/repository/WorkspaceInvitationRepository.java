package com.example.taxone.repository;

import com.example.taxone.entity.WorkspaceInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, UUID> {
    List<WorkspaceInvitation> findAllByWorkspaceId(UUID workspaceUUID);

    Optional<WorkspaceInvitation> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
