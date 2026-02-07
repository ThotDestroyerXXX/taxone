package com.example.taxone.repository;

import com.example.taxone.entity.InvitationStatus;
import com.example.taxone.entity.WorkspaceInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, UUID> {
    List<WorkspaceInvitation> findAllByWorkspaceId(UUID workspaceUUID);

    Optional<WorkspaceInvitation> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Modifying
    @Query("UPDATE WorkspaceInvitation wi SET wi.status = :newStatus WHERE wi.email = :email AND wi.status = :oldStatus")
    void updateStatusByEmailAndStatus(
            @Param("email") String email,
            @Param("oldStatus") InvitationStatus oldStatus,
            @Param("newStatus") InvitationStatus newStatus
    );

    @Modifying
    @Query("""
    UPDATE WorkspaceInvitation wi
    SET wi.status = :newStatus
    WHERE wi.workspace.id = :workspaceId
    AND wi.email = :email
    AND wi.status = :oldStatus
    """)
    void expirePendingInvites(
            @Param("workspaceId") UUID workspaceId,
            @Param("email") String email,
            @Param("oldStatus") InvitationStatus oldStatus,
            @Param("newStatus") InvitationStatus newStatus
    );

    Optional<WorkspaceInvitation> findByIdAndEmail(UUID workspaceInvitationUUID, String email);
}
