package com.example.taxone.dto.response;


import com.example.taxone.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private UUID id;
    private UserResponse reporter;
    private List<UserResponse> assignees;
    private TaskResponse parentTask;
    private String title;
    private String description;
    private String taskKey;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private Date dueDate;
    private Integer estimatedHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
