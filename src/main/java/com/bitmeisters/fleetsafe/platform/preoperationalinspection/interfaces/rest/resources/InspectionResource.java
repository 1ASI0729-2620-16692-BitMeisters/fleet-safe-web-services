package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response resource representing a pre-operational inspection.")
public record InspectionResource(
        @Schema(description = "Persistent identifier") UUID id,
        @Schema(description = "Inspected vehicle") UUID vehicleId,
        @Schema(description = "Driver who performed it") UUID driverId,
        @Schema(description = "Odometer reading in kilometers") Integer odometer,
        @Schema(description = "Status of the inspection", allowableValues = {"IN_PROGRESS", "COMPLETED"}) String status,
        @Schema(description = "Moment it started") Instant startedAt,
        @Schema(description = "Moment it was completed") Instant completedAt,
        @Schema(description = "Results registered so far") List<InspectionResultResource> results) {}
