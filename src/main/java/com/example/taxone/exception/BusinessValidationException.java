package com.example.taxone.exception;

import com.example.taxone.dto.response.ApiErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BusinessValidationException extends RuntimeException {

    private final List<ApiErrorResponse.FieldError> errors;

    public BusinessValidationException(String field, String message) {
        super("Validation failed");
        this.errors = List.of(
                ApiErrorResponse.FieldError.builder()
                        .field(field)
                        .message(message)
                        .build()
        );
    }
}

