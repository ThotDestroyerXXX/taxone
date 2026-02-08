package com.example.taxone.controller;


import com.example.taxone.dto.request.*;
import com.example.taxone.dto.response.TaskResponse;
import com.example.taxone.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String taskId) {
        TaskResponse response = taskService.getTask(taskId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String taskId,
                                                   @RequestBody @Valid TaskUpdateRequest taskUpdateRequest) {
        TaskResponse response = taskService.updateTask(taskId, taskUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> changeTaskStatus(
            @PathVariable String taskId,
            @RequestBody @Valid TaskChangeStatusRequest taskChangeStatusRequest) {
        TaskResponse response =  taskService.changeTaskStatus(taskId, taskChangeStatusRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable String taskId,
                                                   @RequestBody @Valid TaskAssigneeRequest taskAssigneeRequest) {
        TaskResponse response = taskService.assignTask(taskId, taskAssigneeRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{taskId}/unassign")
    public ResponseEntity<TaskResponse> unassignTask(@PathVariable String taskId,
                                                     @RequestBody @Valid TaskAssigneeRequest taskAssigneeRequest) {
        TaskResponse response = taskService.unassignTask(taskId, taskAssigneeRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{taskId}/duplicate")
    public ResponseEntity<TaskResponse> duplicateTask(@PathVariable String taskId) {
        TaskResponse response =  taskService.duplicateTask(taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assigned-to-me")
    public ResponseEntity<List<TaskResponse>> assignedToMe() {
        List<TaskResponse> responses =  taskService.assignedToMe();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/created-by-me")
    public ResponseEntity<List<TaskResponse>>  createdByMe() {
        List<TaskResponse> responses = taskService.createdByMe();
        return  ResponseEntity.ok(responses);
    }

    @PostMapping("/{taskId}/labels")
    public ResponseEntity<TaskResponse> addLabelToTask(
            @PathVariable String taskId,
            @RequestBody @Valid LabelAssignmentRequest labelAssignmentRequest) {
        TaskResponse response =  taskService.addLabelToTask(taskId, labelAssignmentRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}/labels")
    public ResponseEntity<TaskResponse> deleteLabelFromTask(
            @PathVariable String taskId,
            @RequestBody @Valid LabelAssignmentRequest labelAssignmentRequest) {
        TaskResponse response = taskService.deleteLabelFromTask(taskId, labelAssignmentRequest);
        return ResponseEntity.ok(response);
    }
}
