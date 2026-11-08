package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request resource used to register a document type.")
public record RegisterDocumentTypeResource(
        @Schema(
                description = "Unique code of the document type",
                example = "SOAT",
                maxLength = 30,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{documentation.type.error.code.required}")
        @Size(max = 30, message = "{documentation.type.error.code.size}")
        String code,
        @Schema(
                description = "Readable name of the document type",
                example = "Mandatory traffic accident insurance",
                maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{documentation.type.error.name.required}")
        @Size(max = 120, message = "{documentation.type.error.name.size}")
        String name,
        @Schema(
                description = "Optional explanation of the document type",
                example = "Insurance that every vehicle must carry to circulate in Peru.",
                maxLength = 500)
        @Size(max = 500, message = "{documentation.type.error.description.size}")
        String description,
        @Schema(description = "Whether the vehicle needs this document to operate", example = "true")
        boolean isRequired) {}
