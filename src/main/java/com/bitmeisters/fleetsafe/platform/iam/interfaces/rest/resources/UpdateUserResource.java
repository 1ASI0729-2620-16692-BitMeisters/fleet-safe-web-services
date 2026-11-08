package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request resource used to change a user. Only the fields present are changed.")
public record UpdateUserResource(
        @Schema(description = "First name", example = "Luis", maxLength = 100)
        @Size(min = 1, max = 100, message = "{iam.user.error.name.size}")
        String firstName,
        @Schema(description = "Last name", example = "Huamán", maxLength = 100)
        @Size(min = 1, max = 100, message = "{iam.user.error.name.size}")
        String lastName,
        @Schema(description = "Role of the user", example = "FLEET_SUPERVISOR", allowableValues = {"ADMINISTRATOR", "FLEET_SUPERVISOR", "DRIVER"})
        @Pattern(regexp = "ADMINISTRATOR|FLEET_SUPERVISOR|DRIVER", message = "{iam.user.error.role.pattern}")
        String role,
        @Schema(description = "Whether the user can sign in", example = "false")
        Boolean isActive) {}
