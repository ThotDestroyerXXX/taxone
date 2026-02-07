package com.example.taxone.dto.request;

import com.example.taxone.entity.Task;
import com.example.taxone.validator.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskChangeStatusRequest {

    @ValueOfEnum(enumClass = Task.TaskStatus.class, message = "task status should be valid")
    private String status;
}
