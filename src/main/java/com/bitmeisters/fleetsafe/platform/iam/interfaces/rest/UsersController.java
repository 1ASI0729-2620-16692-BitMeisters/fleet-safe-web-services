package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.queryservices.UserQueryService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUsersByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.CreateUserResource;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.UpdateUserResource;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.UserPageResource;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.UserResource;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.RegisterUserCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.ResponseEntityFromUserCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.UpdateUserCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.UserPageResourceFromEntitiesAssembler;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Users of each company")
public class UsersController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final MessageSource messageSource;

    public UsersController(UserCommandService userCommandService, UserQueryService userQueryService, MessageSource messageSource) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "List the users of the company", description = "Roles: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of users", content = @Content(schema = @Schema(implementation = UserPageResource.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation")})
    public ResponseEntity<UserPageResource> getUsers(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @Parameter(description = "Allowed fields: lastName, email, createdAt.") @RequestParam(defaultValue = "lastName,asc") String sort) {
        var users = userQueryService.handle(new GetUsersByCompanyIdQuery(new CompanyId(principal.getCompanyId()), role, isActive));
        return ResponseEntity.ok(UserPageResourceFromEntitiesAssembler.toResourceFromEntities(users, page, size, sort));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Register a user", description = "Roles: ADMINISTRATOR. The user is created in the company of the administrator.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "The email is already registered")})
    public ResponseEntity<?> createUser(@AuthenticationPrincipal UserDetailsImpl principal, @Valid @RequestBody CreateUserResource resource) {
        var result = userCommandService.handle(RegisterUserCommandFromResourceAssembler.toCommandFromResource(resource, principal.getCompanyId()));
        return ResponseEntityFromUserCommandResultAssembler.toResponseEntityFromResult(
                result, UserResourceFromEntityAssembler::toResourceFromEntity, messageSource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user", description = "Roles: ADMINISTRATOR, or any user reading their own account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation"),
            @ApiResponse(responseCode = "404", description = "No user with that id in the company")})
    public ResponseEntity<?> getUserById(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID id) {
        if (!principal.getId().equals(id) && !isAdministrator(principal)) {
            return problem(HttpStatus.FORBIDDEN, "iam.error.forbidden");
        }
        return userQueryService.handle(new GetUserByIdQuery(id))
                .filter(user -> user.getCompanyId().value().equals(principal.getCompanyId()))
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user)))
                .orElseGet(() -> problem(HttpStatus.NOT_FOUND, "iam.user.error.not-found"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Update a user, including its role and whether it is active",
            description = "Roles: ADMINISTRATOR. Only the fields present in the body are changed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The updated user", content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or empty body"),
            @ApiResponse(responseCode = "404", description = "No user with that id in the company")})
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID id,
                                        @Valid @RequestBody UpdateUserResource resource) {
        var current = userQueryService.handle(new GetUserByIdQuery(id))
                .filter(user -> user.getCompanyId().value().equals(principal.getCompanyId()));
        if (current.isEmpty()) return problem(HttpStatus.NOT_FOUND, "iam.user.error.not-found");
        var result = userCommandService.handle(UpdateUserCommandFromResourceAssembler.toCommandFromResource(current.get(), resource));
        return ResponseEntityFromUserCommandResultAssembler.toResponseEntityFromResult(
                result, UserResourceFromEntityAssembler::toResourceFromEntity, messageSource, HttpStatus.OK);
    }

    private static boolean isAdministrator(UserDetailsImpl principal) {
        return principal.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR"));
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String messageKey) {
        var detail = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }
}
