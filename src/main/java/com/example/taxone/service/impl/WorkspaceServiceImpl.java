package com.example.taxone.service.impl;

import com.example.taxone.dto.request.ProjectRequest;
import com.example.taxone.dto.request.WorkspaceInvitationRequest;
import com.example.taxone.dto.request.WorkspaceMemberRoleRequest;
import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.ProjectResponse;
import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.dto.response.WorkspaceMemberResponse;
import com.example.taxone.dto.response.WorkspaceResponse;
import com.example.taxone.entity.*;
import com.example.taxone.exception.BusinessValidationException;
import com.example.taxone.exception.ForbiddenException;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.ProjectMapper;
import com.example.taxone.mapper.WorkspaceInvitationMapper;
import com.example.taxone.mapper.WorkspaceMapper;
import com.example.taxone.mapper.WorkspaceMemberMapper;
import com.example.taxone.repository.ProjectRepository;
import com.example.taxone.repository.WorkspaceInvitationRepository;
import com.example.taxone.repository.WorkspaceMemberRepository;
import com.example.taxone.repository.WorkspaceRepository;
import com.example.taxone.security.CustomUserDetails;
import com.example.taxone.service.WorkspaceService;
import com.example.taxone.util.ColorUtils;
import com.example.taxone.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceInvitationRepository  workspaceInvitationRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceMemberMapper workspaceMemberMapper;
    private final WorkspaceInvitationMapper workspaceInvitationMapper;
    private final ProjectMapper projectMapper;

    @Override
    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest) {
        User user = getCurrentUser();

        ensureUniqueSlug(workspaceRequest.getSlug());

        Workspace newWorkspace = Workspace
                .builder()
                .name(workspaceRequest.getName())
                .description(workspaceRequest.getDescription())
                .logoUrl(workspaceRequest.getLogoUrl())
                .owner(user)
                .slug(workspaceRequest.getSlug())
                .build();

        WorkspaceMember newMember = WorkspaceMember
                .builder()
                .workspace(newWorkspace)
                .memberType(WorkspaceMember.MemberType.OWNER)
                .user(user)
                .build();

        workspaceRepository.save(newWorkspace);
        workspaceMemberRepository.save(newMember);

        return workspaceMapper.toResponse(newWorkspace);

    }

    @Override
    public List<WorkspaceResponse> getWorkspaces() {
        UUID userId = getCurrentUser().getId();

        List<Workspace> workspaces =
                workspaceRepository.findAllByUserId(userId);

        return workspaceMapper.toResponseList(workspaces);
    }

    @Override
    public WorkspaceResponse getWorkspace(String workspaceId) {
        User user = getCurrentUser();

        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository
                .findByIdAndUserHasAccess(workspaceUUID, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        return workspaceMapper.toResponse(workspace);
    }

    @Override
    public WorkspaceResponse updateWorkspace(String workspaceId, WorkspaceRequest request) {
        User user = getCurrentUser();

        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        ensureOwnerOfWorkspace(user, workspace);
        ensureSlugUniqueForUpdate(request.getSlug(),  workspaceUUID);

        // Update existing entity (IMPORTANT)
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setLogoUrl(request.getLogoUrl());
        workspace.setSlug(request.getSlug());

        Workspace saved = workspaceRepository.save(workspace);

        return workspaceMapper.toResponse(saved);
    }

    @Override
    public void deleteWorkspace(String workspaceId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        ensureOwnerOfWorkspace(user, workspace);

        workspace.setIsActive(false);
        workspaceRepository.save(workspace);
    }

    @Override
    public WorkspaceResponse restoreWorkspace(String workspaceId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        ensureOwnerOfWorkspace(user, workspace);

        workspace.setIsActive(true);
        workspaceRepository.save(workspace);

        return workspaceMapper.toResponse(workspace);
    }

    @Override
    public List<WorkspaceMemberResponse> getMembers(String workspaceId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        ensureWorkspaceMember(user.getId(), workspace.getId());

        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository
                .findAllByWorkspaceId(workspace.getId());
        return workspaceMemberMapper.toResponseList(workspaceMembers);
    }

    @Override
    public WorkspaceInvitationResponse inviteMember(String workspaceId,
                                                    WorkspaceInvitationRequest invitationRequest) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        // ensure workspace is present
        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        // ensure only owner or admin can invite
        ensureRoleInWorkspaceMember(workspaceUUID, user.getId(),
                WorkspaceMember.MemberType.OWNER, WorkspaceMember.MemberType.ADMIN);

        // invite to member
        WorkspaceInvitation newInvite = WorkspaceInvitation
                                        .builder()
                                        .email(invitationRequest.getEmail())
                                        .invitedBy(user)
                                        .memberType(invitationRequest.getMemberType())
                                        .workspace(workspace)
                                        .build();
        workspaceInvitationRepository.save(newInvite);

        return workspaceInvitationMapper.toResponse(newInvite);
    }

    @Override
    public WorkspaceMemberResponse getMember(String workspaceId, String memberId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");
        UUID workspaceMemberId = UUIDUtils.fromString(memberId, "memberId");

        // find member by searching for workspace id and member id
        WorkspaceMember member = workspaceMemberRepository.findByIdAndWorkspaceId(workspaceMemberId, workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        return workspaceMemberMapper.toResponse(member);
    }

    @Override
    public WorkspaceMemberResponse updateMemberRole(String workspaceId, String memberId, WorkspaceMemberRoleRequest memberRoleRequest) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");
        UUID workspaceMemberId = UUIDUtils.fromString(memberId, "memberId");

        // find member by searching for workspace id and member id
        WorkspaceMember member = workspaceMemberRepository.findByIdAndWorkspaceId(workspaceMemberId, workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        // get current user's role in workspace member
        WorkspaceMember currentUserMember = workspaceMemberRepository.findByUserIdAndWorkspaceId(user.getId(), workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        WorkspaceMember.MemberType currentRole = currentUserMember.getMemberType();
        WorkspaceMember.MemberType targetRole = member.getMemberType();
        WorkspaceMember.MemberType newRole = memberRoleRequest.getMemberType();

        checkIsValidWorkspaceRoleChange(currentRole, targetRole, newRole);

        // Update role
        member.setMemberType(newRole);
        workspaceMemberRepository.save(member);

        return workspaceMemberMapper.toResponse(member);
    }

    @Override
    public void deleteMember(String workspaceId, String memberId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");
        UUID workspaceMemberId = UUIDUtils.fromString(memberId, "memberId");

        // find member by searching for workspace id and member id
        WorkspaceMember member = workspaceMemberRepository.findByIdAndWorkspaceId(workspaceMemberId, workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        // user cannot delete its own self
        if(user.getId().equals(member.getId())) {
            throw new ResourceNotFoundException("Member not found");
        }

        ensureRoleInWorkspaceMember(workspaceUUID, user.getId(), WorkspaceMember.MemberType.OWNER);

        workspaceMemberRepository.delete(member);
    }

    @Override
    public List<WorkspaceInvitationResponse> getPendingInvites(String workspaceId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        // ensure user is member of workspace
        ensureWorkspaceMember(user.getId(), workspaceUUID);

        List<WorkspaceInvitation> invitations = workspaceInvitationRepository.findAllByWorkspaceId(workspaceUUID);

        return workspaceInvitationMapper.toResponseList(invitations);
    }

    @Override
    public void cancelInvite(String workspaceId, String invitationId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");
        UUID invitationUUID = UUIDUtils.fromString(invitationId, "invitation");

        ensureRoleInWorkspaceMember(workspaceUUID, user.getId(),
                WorkspaceMember.MemberType.OWNER, WorkspaceMember.MemberType.ADMIN);

        WorkspaceInvitation invite = workspaceInvitationRepository.findByIdAndWorkspaceId(invitationUUID, workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invitation not found"));

        invite.setStatus(InvitationStatus.CANCELLED);
        workspaceInvitationRepository.save(invite);
    }

    @Override
    public ProjectResponse createProject(String workspaceId, ProjectRequest projectRequest) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        ensureRoleInWorkspaceMember(workspaceUUID, user.getId(),
                WorkspaceMember.MemberType.OWNER, WorkspaceMember.MemberType.ADMIN);

        Color color = ColorUtils.hexToColor(projectRequest.getColor());
        Project project = Project
                .builder()
                .projectKey(projectRequest.getProjectKey())
                .color(color)
                .description(projectRequest.getDescription())
                .endDate(projectRequest.getEndDate())
                .isPublic(projectRequest.getIsPublic())
                .name(projectRequest.getName())
                .owner(user)
                .priority(projectRequest.getPriority())
                .startDate(projectRequest.getStartDate())
                .build();

        projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectResponse> getProjects(String workspaceId) {
        User user = getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        ensureWorkspaceMember(user.getId(), workspaceUUID);

        List<Project> projects = projectRepository.findVisibleProjects(workspaceUUID, user.getId());

        return projectMapper.toResponseList(projects);
    }

    // helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new IllegalStateException("User not authenticated");
    }

    private void ensureUniqueSlug(String slug) {
        if(workspaceRepository.existsBySlug(slug)) {
            throw new BusinessValidationException("slug", "Slug must be unique");
        }
    }

    private void ensureOwnerOfWorkspace(User user, Workspace workspace) {
        if (!workspace.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not allowed to update this workspace");
        }
    }

    private void ensureRoleInWorkspaceMember(UUID workspaceId, UUID userId, WorkspaceMember.MemberType... memberTypes) {
        WorkspaceMember member = workspaceMemberRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        boolean hasRole = Arrays.stream(memberTypes)
                .anyMatch(role -> role == member.getMemberType());

        if (!hasRole) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    private void ensureSlugUniqueForUpdate(String slug, UUID workspaceId) {
        if (workspaceRepository.existsBySlugAndIdNot(slug, workspaceId)) {
            throw new BusinessValidationException("slug", "Slug must be unique");
        }
    }

    private void ensureWorkspaceMember(UUID userId, UUID workspaceId) {
        if(!workspaceMemberRepository.existsByUserIdAndWorkspaceId(userId, workspaceId)) {
            throw new ForbiddenException("You are not a member of this workspace");
        }
    }

    private void checkIsValidWorkspaceRoleChange(WorkspaceMember.MemberType currentRole,
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
}
