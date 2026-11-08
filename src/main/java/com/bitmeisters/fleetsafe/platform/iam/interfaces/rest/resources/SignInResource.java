package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request resource used to sign in.")
public record SignInResource(
        @Schema(description = "Email of the user", example = "supervisor@transportesandinos.pe",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{iam.user.error.email.required}")
        String email,
        @Schema(description = "Password of the user", example = "S3cure!pass", format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{iam.user.error.password.required}")
        String password) {}
