package com.example.taxone.service.impl;

import com.example.taxone.dto.request.ProjectInvitationRequest;
import com.example.taxone.dto.request.ProjectMemberRoleRequest;
import com.example.taxone.dto.request.ProjectRequest;
import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.ProjectMemberResponse;
import com.example.taxone.dto.response.ProjectResponse;
import com.example.taxone.entity.*;
import com.example.taxone.exception.BusinessValidationException;
import com.example.taxone.exception.ForbiddenException;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.ProjectInvitationMapper;
import com.example.taxone.mapper.ProjectMapper;
import com.example.taxone.mapper.ProjectMemberMapper;
import com.example.taxone.repository.ProjectInvitationRepository;
import com.example.taxone.repository.ProjectMemberRepository;
import com.example.taxone.repository.ProjectRepository;
import com.example.taxone.repository.TaskRepository;
import com.example.taxone.security.CustomUserDetails;
import com.example.taxone.service.ProjectService;
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
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final ProjectInvitationRepository projectInvitationRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final ProjectInvitationMapper projectInvitationMapper;

    @Override
    public ProjectResponse getProject(String projectId) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        ensureProjectMemberOrIsPublic(projectUUID, user.getId());

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse updateProject(String projectId, ProjectRequest projectRequest) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        ensureRoleInProjectMember(projectUUID, user.getId(), ProjectMember.ProjectMemberType.PROJECT_LEAD);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        Color color = ColorUtils.hexToColor(projectRequest.getColor());

        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setColor(color);
        project.setEndDate(projectRequest.getEndDate());
        project.setIsPublic(projectRequest.getIsPublic());
        project.setPriority(projectRequest.getPriority());
        project.setStartDate(projectRequest.getStartDate());

        if(!projectRequest.getProjectKey().equals(project.getProjectKey())) {
            List<Task> tasks = taskRepository.findAllByProjectId(project.getId());

            for(Task task: tasks) {
                String oldTaskKey =  task.getTaskKey();
                String suffix = task.getTaskKey().substring(oldTaskKey.length());

                String newTaskKey = oldTaskKey + suffix;
                task.setTaskKey(newTaskKey);
            }
            taskRepository.saveAll(tasks);
        }

        project.setProjectKey(project.getProjectKey());

        projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    public ProjectResponse archiveProject(String projectId) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        ensureRoleInProjectMember(projectUUID, user.getId(), ProjectMember.ProjectMemberType.PROJECT_LEAD);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setStatus(Project.ProjectStatus.ARCHIVED);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse holdProject(String projectId) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        ensureRoleInProjectMember(projectUUID, user.getId(), ProjectMember.ProjectMemberType.PROJECT_LEAD);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setStatus(Project.ProjectStatus.ON_HOLD);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse restoreProject(String projectId) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        ensureRoleInProjectMember(projectUUID, user.getId(), ProjectMember.ProjectMemberType.PROJECT_LEAD);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setStatus(Project.ProjectStatus.ACTIVE);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectMemberResponse> getMembers(String projectId) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        ensureProjectMemberOrIsPublic(projectUUID, user.getId());

        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(projectUUID);

        return projectMemberMapper.toResponseList(projectMembers);
    }

    @Override
    public ProjectInvitationResponse inviteMember(String projectId, ProjectInvitationRequest invitationRequest) {
        User user = getCurrentUser();
        UUID projectUUID =  UUIDUtils.fromString(projectId, "project");

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        ensureRoleInProjectMember(projectUUID, user.getId(),
                ProjectMember.ProjectMemberType.PROJECT_LEAD, ProjectMember.ProjectMemberType.CONTRIBUTOR);

        ProjectInvitation newInvite = ProjectInvitation
                .builder()
                .memberType(invitationRequest.getMemberType())
                .invitedBy(user)
                .project(project)
                .email(invitationRequest.getEmail())
                .build();
        projectInvitationRepository.save(newInvite);

        return projectInvitationMapper.toResponse(newInvite);
    }

    @Override
    public ProjectMemberResponse updateMemberRole(String projectId, String memberId,
                                                  ProjectMemberRoleRequest roleRequest) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");
        UUID memberUUID = UUIDUtils.fromString(memberId, "member");

        // find member by searching for workspace id and member id
        ProjectMember member = projectMemberRepository.findByIdAndProjectId(memberUUID, projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        // get current user's role in workspace member
        ProjectMember currentUserMember = projectMemberRepository.findByUserIdAndProjectId(user.getId(), projectUUID)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Member not found"));

        ProjectMember.ProjectMemberType currentRole = currentUserMember.getMemberType();
        ProjectMember.ProjectMemberType targetRole = member.getMemberType();
        ProjectMember.ProjectMemberType newRole = roleRequest.getMemberType();

        checkIsValidWorkspaceRoleChange(currentRole, targetRole, newRole);

        // Update role
        member.setMemberType(newRole);
        projectMemberRepository.save(member);

        return projectMemberMapper.toResponse(member);
    }

    @Override
    public void deleteMember(String projectId, String memberId) {
        User user = getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");
        UUID memberUUID = UUIDUtils.fromString(memberId, "member");

        // find member by searching for workspace id and member id
        ProjectMember member = projectMemberRepository.findByIdAndProjectId(memberUUID, projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        ensureRoleInProjectMember(projectUUID, user.getId(), ProjectMember.ProjectMemberType.PROJECT_LEAD);

        projectMemberRepository.delete(member);
    }

    // helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new IllegalStateException("User not authenticated");
    }

    private void ensureProjectMemberOrIsPublic(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        if(!project.getIsPublic()) {
            ProjectMember member = projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                    .orElseThrow(() ->
                            new ForbiddenException("You are not allowed to view this project"));
        }
    }

    private void ensureRoleInProjectMember(UUID projectId, UUID userId, ProjectMember.ProjectMemberType... memberTypes) {
        ProjectMember member = projectMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        boolean hasRole = Arrays.stream(memberTypes)
                .anyMatch(role -> role == member.getMemberType());

        if (!hasRole) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    private void checkIsValidWorkspaceRoleChange(ProjectMember.ProjectMemberType currentRole,
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

}
