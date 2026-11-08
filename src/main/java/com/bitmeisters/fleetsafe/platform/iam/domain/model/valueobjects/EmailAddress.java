package com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

    public static final int MAX_LENGTH = 254;
    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("iam.user.error.email.required");
        }
        value = value.strip().toLowerCase(Locale.ROOT);
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("iam.user.error.email.size");
        }
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("iam.user.error.email.format");
        }
    }
}
