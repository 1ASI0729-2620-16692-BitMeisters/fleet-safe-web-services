package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import lombok.Getter;

@Getter
public enum MediaType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png");
    private final String value;
    MediaType(String value) {
        this.value = value;
    }

    public static MediaType fromString(String value) {
        for (MediaType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("inspection.evidence.error.media-type.unsupported");
    }
}