package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request resource used to register the company of the signed-in administrator.")
public record RegisterCompanyResource(
        @Schema(
                description = "Legal name of the company",
                example = "Transportes Andinos S.A.C.",
                maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.company.error.name.required}")
        @Size(max = 120, message = "{fleet.company.error.name.size}")
        String name,
        @Schema(
                description = "Tax identifier of the company, unique among companies",
                example = "20123456789",
                maxLength = 30,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.company.error.tax-id.required}")
        @Size(max = 30, message = "{fleet.company.error.tax-id.size}")
        String taxId,
        @Schema(description = "Address of the company", example = "Lima, Peru", maxLength = 200)
        @Size(max = 200, message = "{fleet.company.error.address.size}")
        String address,
        @Schema(description = "Contact phone", example = "+51 999 999 999", maxLength = 30)
        @Size(max = 30, message = "{fleet.company.error.phone.size}")
        String phone,
        @Schema(description = "Contact email", example = "contacto@transportesandinos.pe", maxLength = 120)
        @Email(message = "{fleet.company.error.email.invalid}")
        @Size(max = 120, message = "{fleet.company.error.email.size}")
        String email) {}
