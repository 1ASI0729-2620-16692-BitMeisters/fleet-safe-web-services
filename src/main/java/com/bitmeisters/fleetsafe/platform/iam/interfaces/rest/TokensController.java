package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.SignInResource;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.ResponseEntityFromUserCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tokens", description = "Sign in by creating an access token")
public class TokensController {

    private final UserCommandService userCommandService;
    private final MessageSource messageSource;

    public TokensController(UserCommandService userCommandService, MessageSource messageSource) {
        this.userCommandService = userCommandService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @SecurityRequirements
    @Operation(
            summary = "Sign in",
            description = "Checks the email and password and returns a JWT to send as Authorization: Bearer <token>.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request"),
            @ApiResponse(responseCode = "401", description = "Wrong email or password, or inactive user")})
    public ResponseEntity<?> createToken(@Valid @RequestBody SignInResource resource) {
        var result = userCommandService.handle(SignInCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityFromUserCommandResultAssembler.toResponseEntityFromResult(
                result, AuthenticatedUserResourceFromEntityAssembler::toResourceFromEntity, messageSource, HttpStatus.OK);
    }
}
