package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.CompanyCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.CompanyQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetCompanyByIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.CompanyResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.RegisterCompanyResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.FleetValueObjectFromStringAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.RegisterCompanyCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromCompanyCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromCompanyQueryResultAssembler;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/companies", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fleet Management", description = "Companies, fleets, vehicles, drivers and their assignments")
public class CompaniesController {

    private final CompanyCommandService companyCommandService;
    private final CompanyQueryService companyQueryService;
    private final MessageSource messageSource;

    public CompaniesController(CompanyCommandService companyCommandService,
                               CompanyQueryService companyQueryService,
                               MessageSource messageSource) {
        this.companyCommandService = companyCommandService;
        this.companyQueryService = companyQueryService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Register the company of the signed-in administrator",
            description = "Roles: ADMINISTRATOR. The company takes the identifier of the administrator's token, so the "
                    + "users of the company and its fleets refer to the same company. It can be done only once.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company registered",
                    content = @Content(schema = @Schema(implementation = CompanyResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "The company or its tax identifier is already registered")})
    public ResponseEntity<?> registerCompany(@AuthenticationPrincipal UserDetailsImpl principal,
                                             @Valid @RequestBody RegisterCompanyResource resource) {
        var command = RegisterCompanyCommandFromResourceAssembler.toCommandFromResource(principal.getCompanyId(), resource);
        return ResponseEntityFromCompanyCommandResultAssembler.toResponseEntityFromResult(
                companyCommandService.handle(command), messageSource, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the company of the signed-in user", description = "Roles: any authenticated user of the company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The company",
                    content = @Content(schema = @Schema(implementation = CompanyResource.class))),
            @ApiResponse(responseCode = "404", description = "The company has not been registered yet")})
    public ResponseEntity<?> getMyCompany(@AuthenticationPrincipal UserDetailsImpl principal) {
        var query = new GetCompanyByIdQuery(FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()));
        return ResponseEntityFromCompanyQueryResultAssembler.toResponseEntityFromOptional(
                companyQueryService.handle(query), messageSource);
    }
}
