package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response resource representing an observation of a result.")
public record ObservationResource(
        @Schema(description = "Persistent identifier") UUID id,
        @Schema(description = "What was found") String description,
        @Schema(description = "User who recorded it") UUID createdBy,
        @Schema(description = "Moment it was recorded") Instant createdAt,
        @Schema(description = "Photos that support it") List<EvidenceResource> evidences) {}
