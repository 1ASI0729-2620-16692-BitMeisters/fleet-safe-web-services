package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Position of a page within the results.")
public record PageMetadataResource(
        @Schema(description = "Zero-based page number", example = "0") int number,
        @Schema(description = "Page size", example = "20") int size,
        @Schema(description = "Total number of results", example = "1") long totalElements,
        @Schema(description = "Total number of pages", example = "1") int totalPages) {}
