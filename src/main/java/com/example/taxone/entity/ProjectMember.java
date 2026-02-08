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
@Table(name = "project_members")
public class ProjectMember {

    public enum ProjectMemberType {
        PROJECT_LEAD(EnumSet.allOf(ProjectPermission.class)),
        CONTRIBUTOR(EnumSet.of(
                ProjectPermission.PROJECT_VIEW,
                ProjectPermission.MEMBER_VIEW,
                ProjectPermission.MEMBER_INVITE,
                ProjectPermission.TASK_CREATE,
                ProjectPermission.TASK_VIEW,
                ProjectPermission.TASK_UPDATE,
                ProjectPermission.TASK_DELETE,
                ProjectPermission.TASK_ASSIGN,
                ProjectPermission.TASK_UNASSIGN,
                ProjectPermission.LABEL_VIEW,
                ProjectPermission.INVITATION_VIEW,
                ProjectPermission.INVITATION_CANCEL
        )),
        VIEWER(EnumSet.of(
                ProjectPermission.PROJECT_VIEW,
                ProjectPermission.MEMBER_VIEW,
                ProjectPermission.TASK_VIEW,
                ProjectPermission.LABEL_VIEW
        ));

        private final Set<ProjectPermission> permissions;

        ProjectMemberType(Set<ProjectPermission> permissions) {
            this.permissions = permissions;
        }

        public boolean has(ProjectPermission permission) {
            return permissions.contains(permission);
        }

        public Set<ProjectPermission> getPermissions() {
            return permissions;
        }

        public boolean isHigherThan(ProjectMemberType other) {
            return this.ordinal() < other.ordinal();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id",  nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "added_by", nullable = true)
    private User addedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false)
    @Builder.Default
    private ProjectMemberType memberType =  ProjectMemberType.VIEWER;

    @CreationTimestamp
    @Column(name = "added_at",  nullable = false, updatable = false)
    private LocalDateTime addedAt;
}
