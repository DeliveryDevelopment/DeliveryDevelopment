package com.laioffer.delivery.common;

import org.springframework.util.StringUtils;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    public static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
