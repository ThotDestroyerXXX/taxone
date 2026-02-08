package com.example.taxone.mapper;

import com.example.taxone.dto.response.TaskResponse;
import com.example.taxone.entity.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LabelMapper.class})
public interface TaskMapper {

    TaskResponse toResponse(Task task);

    Task toEntity(TaskResponse taskResponse);

    default List<TaskResponse> toResponseList(List<Task> tasks) {
        if(tasks == null) return List.of();

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }
}
