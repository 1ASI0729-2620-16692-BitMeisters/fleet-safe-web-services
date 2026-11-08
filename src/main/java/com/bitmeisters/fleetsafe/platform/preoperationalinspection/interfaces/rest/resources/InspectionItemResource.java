package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Response resource representing a checklist inspection item in the catalog.
 */
@Schema(description = "Response resource representing an inspection item.")
public record InspectionItemResource(
        @Schema(description = "Persistent identifier", example = "496e45e1-43d4-59dc-ae40-0b5e4300743b")
        UUID id,

        @Schema(description = "Standardized code of the item", example = "BRK-01")
        String code,

        @Schema(description = "Descriptive name of the item", example = "Service and parking brake functionality")
        String name,

        @Schema(description = "Detailed verification criteria or instructions", example = "Verify pedal travel, response, and handbrake holding capacity.")
        String description,

        @Schema(description = "Category classification", example = "SAFETY_COMPONENT")
        String category,

        @Schema(description = "Vehicle system", example = "BRAKES")
        String system,

        @Schema(description = "Whether the item directly impacts operational safety", example = "true")
        boolean isSafetyComponent,

        @Schema(description = "Whether photographic evidence is mandatory when failing or observed", example = "true")
        boolean requiresEvidence,

        @Schema(description = "Display sequence order for checklist rendering", example = "1")
        int displayOrder,

        @Schema(description = "Operational state indicating whether the item is active in the catalog", example = "true")
        boolean isActive,

        @Schema(description = "Creation timestamp")
        Instant createdAt,

        @Schema(description = "Last update timestamp")
        Instant updatedAt) {
}
