package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Response resource representing the result of an inspection item.")
public record InspectionResultResource(
        @Schema(description = "Persistent identifier") UUID id,
        @Schema(description = "Catalog item evaluated") UUID inspectionItemId,
        @Schema(description = "Name of the item at the moment of the inspection") String itemName,
        @Schema(description = "Category of the item at the moment of the inspection") String itemCategory,
        @Schema(description = "Result of the item", allowableValues = {"OK", "OBSERVED", "FAIL"}) String result,
        @Schema(description = "Observations of the result") List<ObservationResource> observations) {}
