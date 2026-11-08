package com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects;

import java.util.regex.Pattern;

public record FileUrl(String value) {

    public static final int MAX_LENGTH = 500;
    private static final Pattern HTTP_URL = Pattern.compile("^https?://\\S+$", Pattern.CASE_INSENSITIVE);

    public FileUrl {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("documentation.document.error.file-url.required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("documentation.document.error.file-url.size");
        }
        if (!HTTP_URL.matcher(value).matches()) {
            throw new IllegalArgumentException("documentation.document.error.file-url.pattern");
        }
    }
}
