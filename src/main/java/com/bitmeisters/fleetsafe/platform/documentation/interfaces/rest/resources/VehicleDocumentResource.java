package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Response resource representing a vehicle document.")
public record VehicleDocumentResource(
        @Schema(description = "Persistent identifier", example = "2b4d6f8a-1c3e-4a5b-9d7f-0e2c4a6b8d1f")
        UUID id,
        @Schema(description = "Vehicle the document belongs to", example = "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d")
        UUID vehicleId,
        @Schema(description = "Type of the document", example = "7c1e2d3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f")
        UUID documentTypeId,
        @Schema(description = "Number printed on the document", example = "SOAT-2026-004512")
        String number,
        @Schema(description = "Date the document was issued", example = "2026-01-15")
        LocalDate issueDate,
        @Schema(description = "Date the document expires", example = "2027-01-14")
        LocalDate expirationDate,
        @Schema(description = "Validity derived from the expiration date", example = "VALID", allowableValues = {"VALID", "EXPIRING", "EXPIRED"})
        String status,
        @Schema(description = "Optional link to the scanned document", example = "https://storage.example.com/documents/soat-2026-004512.pdf")
        String fileUrl,
        @Schema(description = "When the document was registered", example = "2026-10-28T14:00:00Z")
        Instant createdAt,
        @Schema(description = "When the document was last updated", example = "2026-10-28T14:00:00Z")
        Instant updatedAt) {}
