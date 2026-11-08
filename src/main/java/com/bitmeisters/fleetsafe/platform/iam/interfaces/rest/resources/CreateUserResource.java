package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request resource used by an administrator to register a user in their company.")
public record CreateUserResource(
        @Schema(description = "Email of the user", example = "driver@transportesandinos.pe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{iam.user.error.email.required}")
        String email,
        @Schema(description = "Initial password, 8 to 72 characters", example = "S3cure!pass", format = "password",
                minLength = 8, maxLength = 72, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{iam.user.error.password.required}")
        @Size(min = 8, max = 72, message = "{iam.user.error.password.size}")
        String password,
        @Schema(description = "First name", example = "Luis", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{iam.user.error.first-name.required}")
        @Size(max = 100, message = "{iam.user.error.name.size}")
        String firstName,
        @Schema(description = "Last name", example = "Huamán", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{iam.user.error.last-name.required}")
        @Size(max = 100, message = "{iam.user.error.name.size}")
        String lastName,
        @Schema(description = "Role of the user", example = "DRIVER", allowableValues = {"ADMINISTRATOR", "FLEET_SUPERVISOR", "DRIVER"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{iam.user.error.role.required}")
        @Pattern(regexp = "ADMINISTRATOR|FLEET_SUPERVISOR|DRIVER", message = "{iam.user.error.role.pattern}")
        String role) {}
