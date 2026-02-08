package com.example.taxone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "workspace_members")
public class WorkspaceMember {

    public enum MemberType {
        OWNER(EnumSet.allOf(WorkspacePermission.class)),
        ADMIN(EnumSet.of(
                WorkspacePermission.WORKSPACE_VIEW,
                WorkspacePermission.WORKSPACE_UPDATE,
                WorkspacePermission.MEMBER_VIEW,
                WorkspacePermission.MEMBER_INVITE,
                WorkspacePermission.MEMBER_DELETE,
                WorkspacePermission.MEMBER_UPDATE,
                WorkspacePermission.PROJECT_CREATE,
                WorkspacePermission.PROJECT_VIEW,
                WorkspacePermission.INVITATION_VIEW,
                WorkspacePermission.INVITATION_CANCEL
        )),
        MEMBER(EnumSet.of(
                WorkspacePermission.WORKSPACE_VIEW,
                WorkspacePermission.MEMBER_VIEW,
                WorkspacePermission.PROJECT_VIEW,
                WorkspacePermission.INVITATION_VIEW
        )),
        VIEWER(EnumSet.of(
                WorkspacePermission.WORKSPACE_VIEW,
                WorkspacePermission.MEMBER_VIEW,
                WorkspacePermission.PROJECT_VIEW
        ));

        private final Set<WorkspacePermission> permissions;

        MemberType(Set<WorkspacePermission> permissions) {
            this.permissions = permissions;
        }

        public boolean has(WorkspacePermission permission) {
            return permissions.contains(permission);
        }

        public Set<WorkspacePermission> getPermissions() {
            return permissions;
        }

        public boolean isHigherThan(MemberType other) {
            return this.ordinal() < other.ordinal();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, unique = true,  nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "workspace_id",   nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    // owner does not have the invited_by
    @ManyToOne(fetch = FetchType.LAZY,  optional = true)
    @JoinColumn(name = "invited_by",   nullable = true)
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type",   nullable = false)
    private MemberType memberType;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
}
