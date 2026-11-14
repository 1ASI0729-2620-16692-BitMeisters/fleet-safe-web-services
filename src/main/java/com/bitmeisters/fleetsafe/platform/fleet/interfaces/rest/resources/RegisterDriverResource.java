package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request resource used to register the driver data of a user account.")
public record RegisterDriverResource(
        @Schema(
                description = "User account of the driver, as created in Identity and Access.",
                example = "2c3d4e5f-6a7b-4c8d-9e0f-1a2b3c4d5e6f",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{fleet.driver.error.user-id.required}")
        UUID userId,
        @Schema(
                description = "Number of the driving license. It is stored in upper case.",
                example = "Q12345678",
                maxLength = 20,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.driver.error.license-number.required}")
        @Size(max = 20, message = "{fleet.driver.error.license-number.size}")
        String licenseNumber,
        @Schema(
                description = "Day the driving license expires. It is in force on that day.",
                example = "2028-05-30",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{fleet.driver.error.license-expiration.required}")
        LocalDate licenseExpirationDate) {}
