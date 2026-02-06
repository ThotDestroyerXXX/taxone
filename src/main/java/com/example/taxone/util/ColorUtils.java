package com.example.taxone.util;

import com.example.taxone.exception.BusinessValidationException;

import java.awt.*;

public final class ColorUtils {
    public static Color hexToColor(String hex) {
        try {
            return Color.decode(hex);
        } catch (NumberFormatException ex) {
            throw new BusinessValidationException("color", "Invalid hex color");
        }
    }
}
