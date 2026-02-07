package com.example.taxone.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssigneeRequest {
    @NotEmpty(message = "At least one assignee is required")
    @Size(min = 1, max = 10, message = "Can assign between 1 and 10 users")
    private List<UUID> assigneeIds;
}
