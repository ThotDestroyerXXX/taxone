package com.example.taxone.repository;

import com.example.taxone.entity.InvitationStatus;
import com.example.taxone.entity.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, UUID> {

    Optional<ProjectInvitation> findByIdAndEmail(UUID projectInvitationUUID, String email);

    List<ProjectInvitation> findAllByEmailAndStatus(String email, InvitationStatus status);

    @Modifying
    @Query("UPDATE ProjectInvitation pi SET pi.status = :newStatus WHERE pi.email = :email AND pi.status = :oldStatus")
    void updateStatusByEmailAndStatus(
            @Param("email") String email,
            @Param("oldStatus") InvitationStatus oldStatus,
            @Param("newStatus") InvitationStatus newStatus
    );

    @Modifying
    @Query("""
    UPDATE ProjectInvitation pi
    SET pi.status = :newStatus
    WHERE pi.project.id = :projectId
    AND pi.email = :email
    AND pi.status = :oldStatus
    """)
    void expirePendingInvites(
            @Param("projectId") UUID projectId,
            @Param("email") String email,
            @Param("oldStatus") InvitationStatus oldStatus,
            @Param("newStatus") InvitationStatus newStatus
    );

    List<ProjectInvitation> findAllByProjectIdAndEmailAndStatusIn(UUID projectId, String email, List<InvitationStatus> status);
}
