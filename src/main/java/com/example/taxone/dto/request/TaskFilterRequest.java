package com.example.taxone.dto.request;

import com.example.taxone.entity.Task;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class TaskFilterRequest {

    private Task.TaskStatus status;
    private Task.TaskPriority priority;

    private Date dueBefore;
    private Date dueAfter;

    private List<UUID> labelIds;
}
