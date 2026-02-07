package com.example.taxone.util;

import com.example.taxone.exception.BusinessValidationException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
public final class DateUtils {
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parseToDate(String dateString, String fieldName) {
        try {
            // Parse the string into a java.util.Date object
            return formatter.parse(dateString);
        } catch (ParseException e) {
            throw new BusinessValidationException(fieldName, "Invalid" + fieldName);
        }
    }
}
