package com.example.taxone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workspace_invitations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_workspace_invited_by_status", columnNames = {
                        "workspace_id",
                        "invited_by",
                        "status"
                })
        })
public class WorkspaceInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false,  updatable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id",  nullable = false)
    private Workspace workspace;

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by",  nullable = false)
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type",  nullable = false)
    @Builder.Default
    private WorkspaceMember.MemberType memberType =  WorkspaceMember.MemberType.VIEWER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",   nullable = false)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at",  nullable = false,  updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at", nullable = true)
    private LocalDateTime respondedAt;
}
