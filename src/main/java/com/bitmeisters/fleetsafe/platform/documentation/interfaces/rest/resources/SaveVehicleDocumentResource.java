package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request resource used to register or update a vehicle document.")
public record SaveVehicleDocumentResource(
        @Schema(
                description = "Type of the document",
                example = "7c1e2d3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{documentation.document.error.document-type-id.required}")
        UUID documentTypeId,
        @Schema(
                description = "Number printed on the document",
                example = "SOAT-2026-004512",
                maxLength = 60,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{documentation.document.error.number.required}")
        @Size(max = 60, message = "{documentation.document.error.number.size}")
        String number,
        @Schema(
                description = "Date the document was issued, in ISO-8601",
                example = "2026-01-15",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{documentation.document.error.issue-date.required}")
        LocalDate issueDate,
        @Schema(
                description = "Date the document expires, in ISO-8601. It cannot be before the issue date.",
                example = "2027-01-14",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{documentation.document.error.expiration-date.required}")
        LocalDate expirationDate,
        @Schema(
                description = "Optional link to the scanned document",
                example = "https://storage.example.com/documents/soat-2026-004512.pdf",
                maxLength = 500)
        @Size(max = 500, message = "{documentation.document.error.file-url.size}")
        @Pattern(regexp = "^$|^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "{documentation.document.error.file-url.pattern}")
        String fileUrl) {}
