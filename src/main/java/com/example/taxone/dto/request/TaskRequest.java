package com.example.taxone.dto.request;


import com.example.taxone.entity.Task;
import com.example.taxone.entity.WorkspaceMember;
import com.example.taxone.validator.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "title is required")
    @Size(min = 2, max = 100, message = "title must be between 2 and 100 characters")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @ValueOfEnum(enumClass = Task.TaskPriority.class)
    private String priority;

    @NotBlank(message = "due date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    @Positive(message = "estimated hours must be positive")
    private Integer estimatedHours;

    @Positive(message = "order index must be positive")
    private Integer orderIndex;
}
