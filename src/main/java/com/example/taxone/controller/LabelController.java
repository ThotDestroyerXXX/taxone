package com.example.taxone.controller;


import com.example.taxone.dto.request.LabelRequest;
import com.example.taxone.dto.response.LabelResponse;
import com.example.taxone.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/{labelId}")
    public ResponseEntity<LabelResponse> getLabel(@PathVariable String labelId) {
        LabelResponse response = labelService.getLabel(labelId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{labelId}")
    public ResponseEntity<LabelResponse> updateLabel(@PathVariable String labelId, @RequestBody @Valid LabelRequest labelRequest) {
        LabelResponse response = labelService.updateLabel(labelId, labelRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<LabelResponse> deleteLabel(@PathVariable String labelId) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }
}
