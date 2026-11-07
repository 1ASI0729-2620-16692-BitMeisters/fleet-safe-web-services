package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response resource representing a company.")
public record CompanyResource(
        @Schema(description = "Identifier of the company, the same one its users carry in their token",
                example = "7c1d2e3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f")
        UUID id,
        @Schema(description = "Legal name of the company", example = "Transportes Andinos S.A.C.")
        String name,
        @Schema(description = "Tax identifier of the company", example = "20123456789")
        String taxId,
        @Schema(description = "Address of the company", example = "Lima, Peru")
        String address,
        @Schema(description = "Contact phone", example = "+51 999 999 999")
        String phone,
        @Schema(description = "Contact email", example = "contacto@transportesandinos.pe")
        String email,
        @Schema(description = "Moment the company was registered", example = "2026-10-01T10:00:00Z")
        Instant createdAt) {}
