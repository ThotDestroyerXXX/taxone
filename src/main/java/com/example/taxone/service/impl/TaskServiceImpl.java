package com.example.taxone.service.impl;

import com.example.taxone.dto.request.LabelAssignmentRequest;
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
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureProjectPermission(task.getProject().getId(), user.getId(),
                ProjectPermission.TASK_VIEW);

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(String taskId, TaskUpdateRequest taskUpdateRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureProjectPermission(task.getProject().getId(), user.getId(),
                ProjectPermission.TASK_UPDATE);

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
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureProjectPermission(task.getProject().getId(), user.getId(),
                ProjectPermission.TASK_DELETE);

        taskRepository.delete(task);
    }

    @Override
    public TaskResponse changeTaskStatus(String taskId, TaskChangeStatusRequest taskChangeStatusRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureProjectPermission(task.getProject().getId(), user.getId(),
                ProjectPermission.TASK_UPDATE);

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
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureProjectPermission(task.getProject().getId(), user.getId(),
                ProjectPermission.TASK_CREATE);

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

    @Override
    public TaskResponse addLabelToTask(String taskId, LabelAssignmentRequest labelAssignmentRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureProjectPermission(task.getProject().getId(), user.getId(),
                ProjectPermission.TASK_UPDATE);

        List<Label> labels = permissionHelper.ensureAllLabelExists(labelAssignmentRequest.getLabelIds());

        // Get existing label IDs to avoid duplicates
        List<UUID> existingLabelIds = task.getTaskLabels().stream()
                .map(taskLabel -> taskLabel.getLabel().getId())
                .toList();

        // Filter out labels that are already added
        List<Label> newLabels = labels.stream()
                .filter(label -> !existingLabelIds.contains(label.getId()))
                .toList();

        // Create TaskLabel entities for new labels
        List<TaskLabel> newTaskLabels = newLabels.stream()
                .map(label -> TaskLabel.builder()
                        .task(task)
                        .label(label)
                        .addedBy(user)
                        .build())
                .toList();

        task.getTaskLabels().addAll(newTaskLabels);

        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse deleteLabelFromTask(String taskId, LabelAssignmentRequest labelAssignmentRequest) {
        User user = authenticationHelper.getCurrentUser();
        UUID taskUUID = UUIDUtils.fromString(taskId, "task");

        Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task with id: " + taskUUID + " not found"));

        permissionHelper.ensureProjectPermission(task.getProject().getId(), user.getId(),
                ProjectPermission.TASK_UPDATE);

        List<Label> labels = permissionHelper.ensureAllLabelExists(labelAssignmentRequest.getLabelIds());

        // Get label IDs to remove
        List<UUID> labelIdsToRemove = labels.stream()
                .map(Label::getId)
                .toList();

        // Remove TaskLabels that match the label IDs
        task.getTaskLabels().removeIf(taskLabel ->
                labelIdsToRemove.contains(taskLabel.getLabel().getId())
        );

        // Save task (cascade will delete TaskLabels due to orphanRemoval = true)
        taskRepository.save(task);

        return taskMapper.toResponse(task);
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

        permissionHelper.ensureProjectPermission(
                task.getProject().getId(),
                user.getId(),
                ProjectPermission.TASK_ASSIGN
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
