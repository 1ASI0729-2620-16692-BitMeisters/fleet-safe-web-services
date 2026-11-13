package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request resource used to deactivate an inspection item.")
public record DeactivateInspectionItemResource(
        @Schema(description = "false deactivates the item", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{inspection.item.error.active.required}")
        @AssertFalse(message = "{inspection.item.error.only-deactivation}")
        Boolean isActive) {}
