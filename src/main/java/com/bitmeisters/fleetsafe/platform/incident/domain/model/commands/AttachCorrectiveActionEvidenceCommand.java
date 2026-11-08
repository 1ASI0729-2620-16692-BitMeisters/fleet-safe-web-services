package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.util.UUID;

public record AttachCorrectiveActionEvidenceCommand(
        UUID incidentId,
        UUID correctiveActionId,
        String evidenceUrl) {

    public AttachCorrectiveActionEvidenceCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }
        if (correctiveActionId == null) {
            throw new IllegalArgumentException("incident.corrective-action.error.id.required");
        }
        if (evidenceUrl == null || evidenceUrl.isBlank()) {
            throw new IllegalArgumentException("incident.corrective-action.error.evidence-url.required");
        }
    }
}
