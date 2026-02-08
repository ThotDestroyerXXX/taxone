package com.example.taxone.service.impl;

import com.example.taxone.dto.request.*;
import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.ProjectMemberResponse;
import com.example.taxone.dto.response.ProjectResponse;
import com.example.taxone.dto.response.TaskResponse;
import com.example.taxone.entity.*;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.ProjectInvitationMapper;
import com.example.taxone.mapper.ProjectMapper;
import com.example.taxone.mapper.ProjectMemberMapper;
import com.example.taxone.mapper.TaskMapper;
import com.example.taxone.repository.*;
import com.example.taxone.service.ProjectService;
import com.example.taxone.util.AuthenticationHelper;
import com.example.taxone.util.DateUtils;
import com.example.taxone.util.PermissionHelper;
import com.example.taxone.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.*;
import java.util.List;

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
    private final TaskMapper taskMapper;

    private final AuthenticationHelper authenticationHelper;
    private final PermissionHelper permissionHelper;

    @Override
    public ProjectResponse getProject(String projectId) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectMemberOrIsPublic(projectUUID, user.getId());

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse updateProject(String projectId, ProjectRequest projectRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.PROJECT_UPDATE);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setColor(projectRequest.getColor());
        project.setEndDate(DateUtils.parseToDate(projectRequest.getEndDate(), "endDate"));
        project.setIsPublic(projectRequest.getIsPublic());
        project.setPriority(Project.ProjectPriority.valueOf(projectRequest.getPriority()));
        project.setStartDate(DateUtils.parseToDate(projectRequest.getStartDate(), "startDate"));

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
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.PROJECT_ARCHIVE);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setStatus(Project.ProjectStatus.ARCHIVED);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse holdProject(String projectId) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.PROJECT_UPDATE);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setStatus(Project.ProjectStatus.ON_HOLD);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse restoreProject(String projectId) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.PROJECT_RESTORE);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        project.setStatus(Project.ProjectStatus.ACTIVE);
        projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectMemberResponse> getMembers(String projectId) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectMemberOrIsPublic(projectUUID, user.getId());

        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(projectUUID);

        return projectMemberMapper.toResponseList(projectMembers);
    }

    @Override
    @Transactional
    public ProjectInvitationResponse inviteMember(String projectId, ProjectInvitationRequest invitationRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID =  UUIDUtils.fromString(projectId, "project");

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.MEMBER_INVITE);

        // permissionHelper.ensure no duplicate invite
        permissionHelper.ensureOnlyInviteNonMember(projectUUID, invitationRequest.getEmail());

        // change status of other pending status by invited by to expired
        projectInvitationRepository.expirePendingInvites(
                projectUUID,
                invitationRequest.getEmail(),
                InvitationStatus.PENDING,
                InvitationStatus.EXPIRED
        );

        ProjectInvitation newInvite = ProjectInvitation
                .builder()
                .memberType(ProjectMember.ProjectMemberType.valueOf(invitationRequest.getMemberType()))
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
        User user = authenticationHelper.getCurrentUser();
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
        ProjectMember.ProjectMemberType newRole = ProjectMember.ProjectMemberType.valueOf(roleRequest.getMemberType());

        permissionHelper.checkIsValidProjectRoleChange(currentRole, targetRole, newRole);

        // Update role
        member.setMemberType(newRole);
        projectMemberRepository.save(member);

        return projectMemberMapper.toResponse(member);
    }

    @Override
    public void deleteMember(String projectId, String memberId) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");
        UUID memberUUID = UUIDUtils.fromString(memberId, "member");

        // find member by searching for workspace id and member id
        ProjectMember member = projectMemberRepository.findByIdAndProjectId(memberUUID, projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.MEMBER_REMOVE);

        projectMemberRepository.delete(member);
    }

    @Override
    public ProjectInvitationResponse cancelProjectInvite(String projectId, String invitationId) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");
        UUID  invitationUUID = UUIDUtils.fromString(invitationId, "invitation");

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.INVITATION_CANCEL);

        // permissionHelper.ensure that the invitation exists
        ProjectInvitation existingProjectInvitation = projectInvitationRepository
                .findById(invitationUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("invitation not found"));

        existingProjectInvitation.setStatus(InvitationStatus.CANCELLED);
        projectInvitationRepository.save(existingProjectInvitation);

        return projectInvitationMapper.toResponse(existingProjectInvitation);
    }

    @Override
    public TaskResponse createTask(String projectId, TaskRequest taskRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.TASK_CREATE);

        Project project = projectRepository.findById(projectUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        String taskKey = permissionHelper.generateNextTaskKey(project.getId());

        // Get assignees if provided
        List<User> assignees = permissionHelper.ensureAllAssigneeExists(taskRequest.getAssigneeIds());

        Task newTask = Task
                .builder()
                .project(project)
                .taskKey(taskKey)
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .estimatedHours(taskRequest.getEstimatedHours())
                .priority(Task.TaskPriority.valueOf(taskRequest.getPriority()))
                .reporter(user)
                .title(taskRequest.getTitle())
                .assignees(assignees)
                .build();

        taskRepository.save(newTask);

        return taskMapper.toResponse(newTask);
    }

    @Override
    public List<TaskResponse> getTasks(String projectId) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID =  UUIDUtils.fromString(projectId, "project");

        // permissionHelper.ensure user has TASK_VIEW permission
        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.TASK_VIEW);

        List<Task> tasks = taskRepository.findAllByProjectId(projectUUID);

        return taskMapper.toResponseList(tasks);
    }

    @Override
    public List<TaskResponse> filterTasks(String projectId, TaskFilterRequest filter) {
        User user = authenticationHelper.getCurrentUser();
        UUID projectUUID = UUIDUtils.fromString(projectId, "project");

        permissionHelper.ensureProjectPermission(projectUUID, user.getId(),
                ProjectPermission.TASK_VIEW);

        List<Task> tasks =
                taskRepository.findByProjectWithFilters(projectUUID, filter);

        return tasks.stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public List<Task> filterTasks(UUID projectId, TaskFilterRequest filter) {
        return taskRepository.findAll(
                TaskSpecification.withFilters(projectId, filter)
        );
    }



    // Helper method to add assignees later
//    public TaskResponse addAssignee(UUID taskId, UUID userId, User currentUser) {
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
//
//        User assignee = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        // Check if already assigned
//        if (task.getAssignees().contains(assignee)) {
//            throw new IllegalStateException("User already assigned to this task");
//        }
//
//        // Add assignee
//        task.getAssignees().add(assignee);
//        Task savedTask = taskRepository.save(task);
//
//        return taskMapper.toResponse(savedTask);
//    }
//
//    // Helper method to remove assignee
//    public TaskResponse removeAssignee(UUID taskId, UUID userId, User currentUser) {
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
//
//        User assignee = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        // Remove assignee
//        boolean removed = task.getAssignees().remove(assignee);
//
//        if (!removed) {
//            throw new IllegalStateException("User not assigned to this task");
//        }
//
//        Task savedTask = taskRepository.save(task);
//
//        return taskMapper.toResponse(savedTask);
//    }

}
