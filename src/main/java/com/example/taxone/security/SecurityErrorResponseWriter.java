package com.example.taxone.security;

import com.example.taxone.dto.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public final class SecurityErrorResponseWriter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private SecurityErrorResponseWriter() {}

    public static void write(
            HttpServletResponse response,
            HttpStatus status,
            String message
    ) throws IOException {

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(status.value())
                .message(message)
                .build();

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
