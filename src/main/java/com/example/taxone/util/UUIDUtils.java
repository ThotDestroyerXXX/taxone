package com.example.taxone.util;

import com.example.taxone.exception.BusinessValidationException;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public final class UUIDUtils {
    public static UUID fromString(String value, String fieldName) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new BusinessValidationException("id", "Invalid " + fieldName + " id");
        }
    }
}
