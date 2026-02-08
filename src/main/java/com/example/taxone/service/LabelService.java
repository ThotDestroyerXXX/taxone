package com.example.taxone.service;

import com.example.taxone.dto.request.LabelRequest;
import com.example.taxone.dto.response.LabelResponse;

public interface LabelService {
    LabelResponse getLabel(String labelId);
    LabelResponse updateLabel(String labelId, LabelRequest labelRequest);
    void deleteLabel(String labelId);
}
