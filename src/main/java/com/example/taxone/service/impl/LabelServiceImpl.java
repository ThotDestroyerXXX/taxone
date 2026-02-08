package com.example.taxone.service.impl;

import com.example.taxone.dto.request.LabelRequest;
import com.example.taxone.dto.response.LabelResponse;
import com.example.taxone.entity.Label;
import com.example.taxone.entity.User;
import com.example.taxone.entity.WorkspaceMember;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.LabelMapper;
import com.example.taxone.repository.LabelRepository;
import com.example.taxone.repository.TaskLabelRepository;
import com.example.taxone.service.LabelService;
import com.example.taxone.util.AuthenticationHelper;
import com.example.taxone.util.PermissionHelper;
import com.example.taxone.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final TaskLabelRepository taskLabelRepository;
    private final LabelMapper labelMapper;

    private final AuthenticationHelper authenticationHelper;
    private final PermissionHelper permissionHelper;

    @Override
    public LabelResponse getLabel(String labelId) {
        User user = authenticationHelper.getCurrentUser();
        UUID labelUUID = UUIDUtils.fromString(labelId, "label");

        Label label =  labelRepository.findById(labelUUID)
                        .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        permissionHelper.ensureWorkspaceMember(user.getId(), label.getWorkspace().getId());

        return labelMapper.toResponse(label);
    }

    @Override
    public LabelResponse updateLabel(String labelId, LabelRequest labelRequest) {
        Label label = getLabelWithAdminPermission(labelId);

        label.setName(labelRequest.getName());
        label.setDescription(labelRequest.getDescription());
        label.setColor(labelRequest.getColor());

        labelRepository.save(label);
        return labelMapper.toResponse(label);
    }

    @Override
    public void deleteLabel(String labelId) {
        Label label = getLabelWithAdminPermission(labelId);

        taskLabelRepository.deleteAllByLabelId(label.getId());
        labelRepository.deleteById(label.getId());
    }

    // helper method
    private Label getLabelWithAdminPermission(String labelId) {
        User user = authenticationHelper.getCurrentUser();
        UUID labelUUID = UUIDUtils.fromString(labelId, "label");

        Label label = labelRepository.findById(labelUUID)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        permissionHelper.ensureRoleInWorkspaceMember(
                label.getWorkspace().getId(),
                user.getId(),
                WorkspaceMember.MemberType.ADMIN,
                WorkspaceMember.MemberType.OWNER
        );

        return label;
    }
}
