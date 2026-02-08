package com.example.taxone.util;

import com.example.taxone.entity.*;
import com.example.taxone.exception.BusinessValidationException;
import com.example.taxone.exception.ForbiddenException;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionHelper {

    private final ProjectMemberRepository projectMemberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final LabelRepository labelRepository;

    public void ensureRoleInProject(UUID projectId, UUID userId, ProjectMember.ProjectMemberType... memberTypes) {
        ProjectMember member = projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in project"));

        boolean hasRole = Arrays.stream(memberTypes)
                .anyMatch(role -> role == member.getMemberType());

        if (!hasRole) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    public void ensureRoleInWorkspace(UUID workspaceId, UUID userId, WorkspaceMember.MemberType... memberTypes) {
        WorkspaceMember member = workspaceMemberRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in workspace"));

        boolean hasRole = Arrays.stream(memberTypes)
                .anyMatch(role -> role == member.getMemberType());

        if (!hasRole) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    public void ensureUniqueSlug(String slug) {
        if(workspaceRepository.existsBySlug(slug)) {
            throw new BusinessValidationException("slug", "Slug must be unique");
        }
    }

    public void ensureOwnerOfWorkspace(User user, Workspace workspace) {
        if (!workspace.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not allowed to update this workspace");
        }
    }

    public void ensureRoleInWorkspaceMember(UUID workspaceId, UUID userId, WorkspaceMember.MemberType... memberTypes) {
        WorkspaceMember member = workspaceMemberRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("You are not a member of this workspace"));

        boolean hasRole = Arrays.stream(memberTypes)
                .anyMatch(role -> role == member.getMemberType());

        if (!hasRole) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    public void ensureSlugUniqueForUpdate(String slug, UUID workspaceId) {
        if (workspaceRepository.existsBySlugAndIdNot(slug, workspaceId)) {
            throw new BusinessValidationException("slug", "Slug must be unique");
        }
    }

    public void ensureWorkspaceMember(UUID userId, UUID workspaceId) {
        if(!workspaceMemberRepository.existsByUserIdAndWorkspaceId(userId, workspaceId)) {
            throw new ForbiddenException("You are not a member of this workspace");
        }
    }

    public void checkIsValidWorkspaceRoleChange(WorkspaceMember.MemberType currentRole,
                                                 WorkspaceMember.MemberType targetRole,
                                                 WorkspaceMember.MemberType newRole) {
        // 1️⃣ Only OWNER or ADMIN can change roles
        if (currentRole == WorkspaceMember.MemberType.MEMBER || currentRole == WorkspaceMember.MemberType.VIEWER) {
            throw new ForbiddenException("You are not allowed to change member roles");
        }

        // 2️⃣ Cannot change role of someone higher than you
        if (targetRole.isHigherThan(currentRole)) {
            throw new ForbiddenException("You cannot modify a member with a higher role");
        }

        // 3️⃣ Cannot assign a role higher than your own
        if (newRole.isHigherThan(currentRole)) {
            throw new ForbiddenException("You cannot assign a role higher than your own");
        }

        // 4️⃣ (Optional but recommended) OWNER role cannot be changed
        if (targetRole == WorkspaceMember.MemberType.OWNER && currentRole != WorkspaceMember.MemberType.OWNER) {
            throw new ForbiddenException("Owner role cannot be modified");
        }

        // 5️⃣ Prevent no-op updates
        if (targetRole == newRole) {
            throw new BusinessValidationException("memberType", "Role is already assigned");
        }
    }

    public void ensureOnlyInviteNonMember(UUID workspaceId, String email) {
        if(workspaceMemberRepository.existsByWorkspace_IdAndUser_Email(workspaceId, email)) {
            throw new IllegalStateException("User is already a member of this workspace");
        }
    }

    public void ensureRoleInProjectMember(UUID projectId, UUID userId, ProjectMember.ProjectMemberType... memberTypes) {
        ProjectMember member = projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        boolean hasRole = Arrays.stream(memberTypes)
                .anyMatch(role -> role == member.getMemberType());

        if (!hasRole) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    public List<User> ensureAllAssigneeExists(List<UUID> assigneeIds) {
        if (assigneeIds != null && !assigneeIds.isEmpty()) {
            List<User> assignees = userRepository.findAllById(assigneeIds);

            // Validate all users exist
            if (assignees.size() != assigneeIds.size()) {
                throw new ResourceNotFoundException("One or more assignees not found");
            }

            return assignees;
        }
        return List.of();
    }

    public List<Label> ensureAllLabelExists(List<UUID> labelIds) {
        if (labelIds != null && !labelIds.isEmpty()) {
            List<Label> labels = labelRepository.findAllById(labelIds);

            // Validate all users exist
            if (labels.size() != labelIds.size()) {
                throw new ResourceNotFoundException("One or more assignees not found");
            }

            return labels;
        }
        return List.of();
    }

    public void ensureProjectMemberOrIsPublic(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        if(!project.getIsPublic()) {
            ProjectMember member = projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                    .orElseThrow(() ->
                            new ForbiddenException("You are not allowed to view this project"));
        }
    }

    public void checkIsValidWorkspaceRoleChange(ProjectMember.ProjectMemberType currentRole,
                                                 ProjectMember.ProjectMemberType targetRole,
                                                 ProjectMember.ProjectMemberType newRole) {
        // 1️⃣ Only OWNER or ADMIN can change roles
        if (currentRole == ProjectMember.ProjectMemberType.VIEWER) {
            throw new ForbiddenException("You are not allowed to change member roles");
        }

        // 2️⃣ Cannot change role of someone higher than you
        if (targetRole.isHigherThan(currentRole)) {
            throw new ForbiddenException("You cannot modify a member with a higher role");
        }

        // 5️⃣ Prevent no-op updates
        if (targetRole == newRole) {
            throw new BusinessValidationException("memberType", "Role is already assigned");
        }
    }

    public String generateNextTaskKey(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        String projectKey = project.getProjectKey(); // e.g., "TXN"

        // Get the last task key
        Optional<String> lastTaskKey = taskRepository.findLastTaskKeyByProject(project.getId());

        int nextNumber;
        if (lastTaskKey.isPresent()) {
            // Extract number from "TXN-100" → 100
            String lastKey = lastTaskKey.get();
            String numberPart = lastKey.substring(projectKey.length() + 1); // Skip "TXN-"
            nextNumber = Integer.parseInt(numberPart) + 1;
        } else {
            // First task
            nextNumber = 1;
        }

        // Format as "TXN-001" (3 digits with leading zeros)
        return String.format("%s-%03d", projectKey, nextNumber);
    }

    public void ensureIsPendingInvitationStatus(InvitationStatus status) {
        if(!status.equals(InvitationStatus.PENDING)) {
            throw new IllegalStateException("Invitation is not pending");
        }
    }
}