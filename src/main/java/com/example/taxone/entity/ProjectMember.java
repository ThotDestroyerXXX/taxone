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
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "project_members")
public class ProjectMember {

    public enum ProjectMemberType {
        PROJECT_LEAD,
        CONTRIBUTOR,
        VIEWER;

        public boolean isHigherThan(ProjectMemberType other) {
            return this.ordinal() < other.ordinal();
        }

        public boolean isLowerThan(ProjectMemberType other) {
            return this.ordinal() > other.ordinal();
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false)
    @Builder.Default
    private ProjectMemberType memberType =  ProjectMemberType.VIEWER;

    @CreationTimestamp
    @Column(name = "added_at",  nullable = false, updatable = false)
    private LocalDateTime addedAt;
}
