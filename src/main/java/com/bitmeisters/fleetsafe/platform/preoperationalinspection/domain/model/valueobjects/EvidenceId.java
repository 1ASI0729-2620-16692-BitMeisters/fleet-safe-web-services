package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import java.util.UUID;

public record EvidenceId(UUID value) {

    public EvidenceId {
        if (value == null) {
            throw new IllegalArgumentException("inspection.evidence.error.id.required");
        }
    }

    public EvidenceId(){
        this(UUID.randomUUID());
    }

}
