package com.example.taxone.service.impl;

import com.example.taxone.dto.request.TaskAssigneeRequest;
import com.example.taxone.dto.request.TaskChangeStatusRequest;
import com.example.taxone.dto.request.TaskUpdateRequest;
import com.example.taxone.dto.response.TaskResponse;
import com.example.taxone.entity.*;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.TaskMapper;
import com.example.taxone.repository.ProjectRepository;
import com.example.taxone.repository.TaskRepository;
import com.example.taxone.service.TaskService;
import com.example.taxone.util.AuthenticationHelper;
import com.example.taxone.util.PermissionHelper;
import com.example.taxone.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    private final TaskMapper taskMapper;

    private final AuthenticationHelper authenticationHelper;
    private final PermissionHelper permissionHelper;

    @Override
    public TaskResponse getTask(String taskId) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new  ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        // permissionHelper.ensure only member of project can see task
        permissionHelper.ensureRoleInProjectMember(task.getProject().getId(), user.getId(),
                ProjectMember.ProjectMemberType.values());

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(String taskId, TaskUpdateRequest taskUpdateRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID =  UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new  ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        // permissionHelper.ensure only project lead can update task
        permissionHelper.ensureRoleInProjectMember(task.getProject().getId(), user.getId(),
                ProjectMember.ProjectMemberType.PROJECT_LEAD);

        task.setDescription(taskUpdateRequest.getDescription());
        task.setDueDate(taskUpdateRequest.getDueDate());
        task.setEstimatedHours(taskUpdateRequest.getEstimatedHours());
        task.setPriority(Task.TaskPriority.valueOf(taskUpdateRequest.getPriority()));
        task.setTitle(taskUpdateRequest.getTitle());

        taskRepository.save(task);

        return  taskMapper.toResponse(task);
    }

    @Override
    public void deleteTask(String taskId) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new  ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        // permissionHelper.ensure only project lead can delete task
        permissionHelper.ensureRoleInProjectMember(task.getProject().getId(), user.getId(),
                ProjectMember.ProjectMemberType.PROJECT_LEAD);

        taskRepository.delete(task);
    }

    @Override
    public TaskResponse changeTaskStatus(String taskId, TaskChangeStatusRequest taskChangeStatusRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new  ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        // permissionHelper.ensure only project lead can change status
        permissionHelper.ensureRoleInProjectMember(task.getProject().getId(), user.getId(),
                ProjectMember.ProjectMemberType.PROJECT_LEAD);

        task.setStatus(Task.TaskStatus.valueOf(taskChangeStatusRequest.getStatus()));
        taskRepository.save(task);

        return  taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse assignTask(String taskId, TaskAssigneeRequest request) {
        return updateTaskAssignment(taskId, request, TaskAssignmentAction.ASSIGN);
    }

    @Override
    public TaskResponse unassignTask(String taskId, TaskAssigneeRequest request) {
        return updateTaskAssignment(taskId, request, TaskAssignmentAction.UNASSIGN);
    }

    @Override
    public TaskResponse duplicateTask(String taskId) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new  ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureRoleInProjectMember(task.getProject().getId(), user.getId(),
                ProjectMember.ProjectMemberType.PROJECT_LEAD, ProjectMember.ProjectMemberType.CONTRIBUTOR);

        Project project = projectRepository.findById(task.getProject().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        String taskKey = permissionHelper.generateNextTaskKey(task.getProject().getId());

        Task newTask = Task
                .builder()
                .project(project)
                .taskKey(taskKey)
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .estimatedHours(task.getEstimatedHours())
                .priority(task.getPriority())
                .reporter(user)
                .title(task.getTitle())
                .assignees(List.of())
                .build();

        taskRepository.save(newTask);
        return taskMapper.toResponse(newTask);
    }

    @Override
    public List<TaskResponse> assignedToMe() {
        User user = authenticationHelper.getCurrentUser();

        List<Task> tasks = taskRepository.findByAssignees_Id(user.getId());

        return taskMapper.toResponseList(tasks);
    }

    @Override
    public List<TaskResponse> createdByMe() {
        User user = authenticationHelper.getCurrentUser();

        List<Task> tasks = taskRepository.findByReporterId(user.getId());

        return taskMapper.toResponseList(tasks);
    }

    private TaskResponse updateTaskAssignment(
            String taskId,
            TaskAssigneeRequest request,
            TaskAssignmentAction action
    ) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureRoleInProjectMember(
                task.getProject().getId(),
                user.getId(),
                ProjectMember.ProjectMemberType.PROJECT_LEAD
        );

        List<User> assignees =
                permissionHelper.ensureAllAssigneeExists(request.getAssigneeIds());

        switch (action) {
            case ASSIGN -> task.setAssignees(assignees);
            case UNASSIGN -> task.getAssignees().removeAll(assignees);
        }

        taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

}
