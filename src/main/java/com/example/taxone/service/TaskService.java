package com.example.taxone.service;

import com.example.taxone.dto.request.TaskAssigneeRequest;
import com.example.taxone.dto.request.TaskChangeStatusRequest;
import com.example.taxone.dto.request.TaskUpdateRequest;
import com.example.taxone.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse getTask(String taskId);
    TaskResponse updateTask(String taskId, TaskUpdateRequest  taskUpdateRequest);
    void deleteTask(String taskId);
    TaskResponse changeTaskStatus(String taskId, TaskChangeStatusRequest taskChangeStatusRequest);
    TaskResponse assignTask(String taskId, TaskAssigneeRequest taskAssigneeRequest);
    TaskResponse unassignTask(String taskId, TaskAssigneeRequest taskAssigneeRequest);
    TaskResponse duplicateTask(String taskId);
    List<TaskResponse> assignedToMe();
    List<TaskResponse> createdByMe();
}
