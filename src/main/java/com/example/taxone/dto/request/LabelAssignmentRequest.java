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
public class LabelAssignmentRequest {
    @NotEmpty(message = "At least one label is required")
    @Size(min = 1, max = 10, message = "Can assign between 1 and 10 labels")
    private List<UUID> labelIds;
}
