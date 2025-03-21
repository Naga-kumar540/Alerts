// DateUtils.java
package com.invoice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public long daysBetween(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return -1;
        }
        return ChronoUnit.DAYS.between(date1, date2);
    }

    public boolean isDateInFuture(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }
}