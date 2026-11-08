package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response resource representing the photo that supports an observation.")
public record EvidenceResource(
        @Schema(description = "Persistent identifier") UUID id,
        @Schema(description = "Address to read the photo, valid for one hour") String fileUrl,
        @Schema(description = "Content type of the photo", example = "image/jpeg") String mediaType,
        @Schema(description = "Moment the photo was attached") Instant uploadedAt) {}
