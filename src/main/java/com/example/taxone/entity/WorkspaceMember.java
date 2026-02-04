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
@Table(name = "workspace_members")
public class WorkspaceMember {

    public enum MemberType {
        OWNER,
        ADMIN,
        MEMBER,
        VIEWER,
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
