package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request resource used to update an existing checklist inspection item.
 */
@Schema(description = "Request resource used to update an inspection item.")
public record UpdateInspectionItemResource(
        @Schema(
                description = "Descriptive name of the inspection item",
                example = "Service and parking brake functionality",
                maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{inspection.item.error.name.required}")
        @Size(max = 120, message = "{inspection.item.error.name.size}")
        String name,

        @Schema(
                description = "Verification criteria or instructions",
                example = "Verify pedal travel, response, and handbrake holding capacity.",
                maxLength = 1000)
        @Size(max = 1000, message = "{inspection.item.error.description.size}")
        String description,

        @Schema(
                description = "Category classification",
                example = "SAFETY_COMPONENT",
                allowableValues = {"COMPONENT", "SAFETY_COMPONENT", "DOCUMENTATION"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{inspection.item.error.category.required}")
        @Pattern(regexp = "COMPONENT|SAFETY_COMPONENT|DOCUMENTATION", message = "{inspection.item.error.category.pattern}")
        String category,

        @Schema(
                description = "Vehicle system",
                example = "BRAKES",
                allowableValues = {"BRAKES", "LIGHTS", "TIRES_AND_SUSPENSION", "ENGINE_AND_FLUIDS", "CABIN", "SAFETY_EQUIPMENT", "BODY_AND_COUPLING", "DOCUMENTATION"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{inspection.item.error.system.required}")
        @Pattern(regexp = "BRAKES|LIGHTS|TIRES_AND_SUSPENSION|ENGINE_AND_FLUIDS|CABIN|SAFETY_EQUIPMENT|BODY_AND_COUPLING|DOCUMENTATION", message = "{inspection.item.error.system.pattern}")
        String system,

        @Schema(description = "Whether this item is critical to operational safety", example = "true")
        boolean isSafetyComponent,

        @Schema(description = "Whether photographic evidence is mandatory when failing or observed", example = "true")
        boolean requiresEvidence,

        @Schema(description = "Display sequence order for checklist rendering", example = "1")
        int displayOrder) {
}
