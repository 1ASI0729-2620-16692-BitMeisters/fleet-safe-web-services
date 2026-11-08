package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response resource representing a document type.")
public record DocumentTypeResource(
        @Schema(description = "Persistent identifier", example = "7c1e2d3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f")
        UUID id,
        @Schema(description = "Unique code of the document type", example = "SOAT")
        String code,
        @Schema(description = "Readable name of the document type", example = "Mandatory traffic accident insurance")
        String name,
        @Schema(description = "Optional explanation of the document type", example = "Insurance that every vehicle must carry to circulate in Peru.")
        String description,
        @Schema(description = "Whether the vehicle needs this document to operate", example = "true")
        boolean isRequired,
        @Schema(description = "Whether the document type can be used", example = "true")
        boolean isActive) {}
