package com.example.taxone.mapper;

import com.example.taxone.dto.response.LabelResponse;
import com.example.taxone.entity.Label;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelResponse toResponse(Label label);

    Label toEntity(LabelResponse labelResponse);

    default List<LabelResponse> toResponseList(List<Label> labels) {
        if(labels == null) return List.of();

        return labels.stream()
                .map(this::toResponse)
                .toList();
    }
}
