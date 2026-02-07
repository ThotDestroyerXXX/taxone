package com.example.taxone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_invitations")
public class ProjectInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",  updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by",  nullable = false)
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type",  nullable = false)
    @Builder.Default
    private ProjectMember.ProjectMemberType memberType = ProjectMember.ProjectMemberType.VIEWER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",   nullable = false)
    @Builder.Default
    private InvitationStatus status =  InvitationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at", nullable = true)
    private LocalDateTime respondedAt;
}
