package com.example.taxone.service.impl;

import com.example.taxone.dto.request.*;
import com.example.taxone.dto.response.*;
import com.example.taxone.entity.*;
import com.example.taxone.entity.Label;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.*;
import com.example.taxone.repository.*;
import com.example.taxone.service.WorkspaceService;
import com.example.taxone.util.AuthenticationHelper;
import com.example.taxone.util.DateUtils;
import com.example.taxone.util.PermissionHelper;
import com.example.taxone.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceInvitationRepository  workspaceInvitationRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final LabelRepository labelRepository;
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceMemberMapper workspaceMemberMapper;
    private final WorkspaceInvitationMapper workspaceInvitationMapper;
    private final ProjectMapper projectMapper;
    
    private final AuthenticationHelper authenticationHelper;
    private final PermissionHelper permissionHelper;
    private final LabelMapper labelMapper;

    @Override
    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest) {
        User user = authenticationHelper.getCurrentUser();

        permissionHelper.ensureUniqueSlug(workspaceRequest.getSlug());

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
        UUID userId = authenticationHelper.getCurrentUser().getId();

        List<Workspace> workspaces =
                workspaceRepository.findAllByUserId(userId);

        return workspaceMapper.toResponseList(workspaces);
    }

    @Override
    public WorkspaceResponse getWorkspace(String workspaceId) {
        User user = authenticationHelper.getCurrentUser();

        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository
                .findByIdAndUserHasAccess(workspaceUUID, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        return workspaceMapper.toResponse(workspace);
    }

    @Override
    public WorkspaceResponse updateWorkspace(String workspaceId, WorkspaceRequest request) {
        User user = authenticationHelper.getCurrentUser();

        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        permissionHelper.ensureOwnerOfWorkspace(user, workspace);
        permissionHelper.ensureSlugUniqueForUpdate(request.getSlug(),  workspaceUUID);

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
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        permissionHelper.ensureOwnerOfWorkspace(user, workspace);

        workspace.setIsActive(false);
        workspaceRepository.save(workspace);
    }

    @Override
    public WorkspaceResponse restoreWorkspace(String workspaceId) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        permissionHelper.ensureOwnerOfWorkspace(user, workspace);

        workspace.setIsActive(true);
        workspaceRepository.save(workspace);

        return workspaceMapper.toResponse(workspace);
    }

    @Override
    public List<WorkspaceMemberResponse> getMembers(String workspaceId) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        permissionHelper.ensureWorkspaceMember(user.getId(), workspace.getId());

        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository
                .findAllByWorkspaceId(workspace.getId());
        return workspaceMemberMapper.toResponseList(workspaceMembers);
    }

    @Override
    @Transactional
    public WorkspaceInvitationResponse inviteMember(String workspaceId,
                                                    WorkspaceInvitationRequest invitationRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        // permissionHelper.ensure workspace is present
        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        // permissionHelper.ensure only owner or admin can invite
        permissionHelper.ensureRoleInWorkspaceMember(workspaceUUID, user.getId(),
                WorkspaceMember.MemberType.OWNER, WorkspaceMember.MemberType.ADMIN);

        permissionHelper.ensureOnlyInviteNonMember(workspaceUUID, invitationRequest.getEmail());

        // change status of other pending status by invited by to expired
        workspaceInvitationRepository.expirePendingInvites(
                workspaceUUID,
                invitationRequest.getEmail(),
                InvitationStatus.PENDING,
                InvitationStatus.EXPIRED
        );

        // invite to member
        WorkspaceInvitation newInvite = WorkspaceInvitation
                                        .builder()
                                        .email(invitationRequest.getEmail())
                                        .invitedBy(user)
                                        .memberType(WorkspaceMember.MemberType.valueOf(invitationRequest.getMemberType()))
                                        .workspace(workspace)
                                        .build();
        workspaceInvitationRepository.save(newInvite);

        return workspaceInvitationMapper.toResponse(newInvite);
    }

    @Override
    public WorkspaceMemberResponse getMember(String workspaceId, String memberId) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");
        UUID workspaceMemberId = UUIDUtils.fromString(memberId, "memberId");

        permissionHelper.ensureWorkspaceMember(user.getId(), workspaceUUID);

        // find member by searching for workspace id and member id
        WorkspaceMember member = workspaceMemberRepository.findByIdAndWorkspaceId(workspaceMemberId, workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        return workspaceMemberMapper.toResponse(member);
    }

    @Override
    public WorkspaceMemberResponse updateMemberRole(String workspaceId, String memberId, WorkspaceMemberRoleRequest memberRoleRequest) {
        User user = authenticationHelper.getCurrentUser();
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
        WorkspaceMember.MemberType newRole = WorkspaceMember.MemberType.valueOf(memberRoleRequest.getMemberType());

        permissionHelper.checkIsValidWorkspaceRoleChange(currentRole, targetRole, newRole);

        // Update role
        member.setMemberType(newRole);
        workspaceMemberRepository.save(member);

        return workspaceMemberMapper.toResponse(member);
    }

    @Override
    public void deleteMember(String workspaceId, String memberId) {
        User user = authenticationHelper.getCurrentUser();
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

        permissionHelper.ensureRoleInWorkspaceMember(workspaceUUID, user.getId(), WorkspaceMember.MemberType.OWNER);

        workspaceMemberRepository.delete(member);
    }

    @Override
    public List<WorkspaceInvitationResponse> getPendingInvites(String workspaceId) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        // permissionHelper.ensure user is member of workspace
        permissionHelper.ensureWorkspaceMember(user.getId(), workspaceUUID);

        List<WorkspaceInvitation> invitations = workspaceInvitationRepository.findAllByWorkspaceId(workspaceUUID);

        return workspaceInvitationMapper.toResponseList(invitations);
    }

    @Override
    public void cancelInvite(String workspaceId, String invitationId) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");
        UUID invitationUUID = UUIDUtils.fromString(invitationId, "invitation");

        permissionHelper.ensureRoleInWorkspaceMember(workspaceUUID, user.getId(),
                WorkspaceMember.MemberType.OWNER, WorkspaceMember.MemberType.ADMIN);

        WorkspaceInvitation invite = workspaceInvitationRepository.findByIdAndWorkspaceId(invitationUUID, workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invitation not found"));

        invite.setStatus(InvitationStatus.CANCELLED);
        workspaceInvitationRepository.save(invite);
    }

    @Override
    public ProjectResponse createProject(String workspaceId, ProjectRequest projectRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        permissionHelper.ensureRoleInWorkspaceMember(workspaceUUID, user.getId(),
                WorkspaceMember.MemberType.OWNER, WorkspaceMember.MemberType.ADMIN);

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("workspace not found"));

        Project project = Project
                .builder()
                .projectKey(projectRequest.getProjectKey())
                .workspace(workspace)
                .color(projectRequest.getColor())
                .description(projectRequest.getDescription())
                .endDate(DateUtils.parseToDate(projectRequest.getEndDate(), "endDate"))
                .isPublic(projectRequest.getIsPublic())
                .name(projectRequest.getName())
                .owner(user)
                .priority(Project.ProjectPriority.valueOf(projectRequest.getPriority()))
                .startDate(DateUtils.parseToDate(projectRequest.getStartDate(), "startDate"))
                .build();

        projectRepository.save(project);

        ProjectMember member = ProjectMember
                .builder()
                .memberType(ProjectMember.ProjectMemberType.PROJECT_LEAD)
                .project(project)
                .user(user)
                .build();

        projectMemberRepository.save(member);

        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectResponse> getProjects(String workspaceId) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        permissionHelper.ensureWorkspaceMember(user.getId(), workspaceUUID);

        List<Project> projects = projectRepository.findVisibleProjects(workspaceUUID, user.getId());

        return projectMapper.toResponseList(projects);
    }

    @Override
    public LabelResponse createLabel(String workspaceId, LabelRequest labelRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        permissionHelper.ensureRoleInWorkspaceMember(workspaceUUID, user.getId(),
                WorkspaceMember.MemberType.OWNER, WorkspaceMember.MemberType.ADMIN);

        Workspace workspace = workspaceRepository.findById(workspaceUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        Label label = Label
                .builder()
                .color(labelRequest.getColor())
                .workspace(workspace)
                .name(labelRequest.getName())
                .description(labelRequest.getDescription())
                .build();

        labelRepository.save(label);

        return labelMapper.toResponse(label);
    }

    @Override
    public List<LabelResponse> getLabels(String workspaceId) {
        User user = authenticationHelper.getCurrentUser();
        UUID workspaceUUID = UUIDUtils.fromString(workspaceId, "workspace");

        permissionHelper.ensureWorkspaceMember(user.getId(), workspaceUUID);

        List<Label> labels = labelRepository.findAllByWorkspaceId(workspaceUUID);

        return labelMapper.toResponseList(labels);
    }
}
