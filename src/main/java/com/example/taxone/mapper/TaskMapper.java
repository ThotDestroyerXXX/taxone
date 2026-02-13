package com.example.taxone.mapper;

import com.example.taxone.dto.response.TaskResponse;
import com.example.taxone.entity.Label;
import com.example.taxone.entity.Task;
import com.example.taxone.entity.TaskLabel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {LabelMapper.class, UserMapper.class})
public abstract class TaskMapper {

    @Mapping(source = "taskLabels", target = "labels", qualifiedByName = "mapTaskLabelsToLabels")
    public abstract TaskResponse toResponse(Task task);

    public abstract Task toEntity(TaskResponse taskResponse);

    public List<TaskResponse> toResponseList(List<Task> tasks) {
        if(tasks == null) return List.of();

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    @Named("mapTaskLabelsToLabels")
    protected List<Label> mapTaskLabelsToLabels(List<TaskLabel> taskLabels) {
        if (taskLabels == null) {
            return List.of();
        }

        return taskLabels.stream()
                .map(TaskLabel::getLabel)
                .collect(Collectors.toList());
    }
}
