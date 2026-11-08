package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultSnapshot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OperationalAuthorizationsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EvaluationRuleCommandService evaluationRuleCommandService;

    @Autowired
    private EvaluationCommandService evaluationCommandService;

    private User supervisor;
    private String supervisorToken;
    private String driverToken;
    private final UUID blockedVehicle = UUID.randomUUID();

    private User register(Role role) {
        return userCommandService.handle(new RegisterUserCommand(new CompanyId(UUID.randomUUID()),
                new EmailAddress(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@andinos.pe"), "S3cure!pass",
                new PersonName("Ana", "Quispe"), role)).success().orElseThrow();
    }

    @BeforeEach
    void blockAVehicle() {
        supervisor = register(Role.FLEET_SUPERVISOR);
        supervisorToken = "Bearer " + tokenService.generateToken(supervisor);
        driverToken = "Bearer " + tokenService.generateToken(register(Role.DRIVER));
        var brakes = new InspectionItemId(UUID.randomUUID());
        evaluationRuleCommandService.handle(new DefineEvaluationRuleCommand(brakes, new RuleName("Brakes"), ResultValue.FAIL, RuleImpact.BLOCKING, null));
        evaluationCommandService.handle(new EvaluateInspectionCommand(new InspectionId(UUID.randomUUID()), new VehicleId(blockedVehicle),
                List.of(new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()), brakes, ResultValue.FAIL)), null));
    }

    @Test
    void listsOnlyTheLatestAuthorizationOfEachVehicle() throws Exception {
        mockMvc.perform(post("/api/v1/vehicles/{id}/block-lifts", blockedVehicle).header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\": \"Urgent delivery\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/operational-authorizations/current").header("Authorization", supervisorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.vehicleId == '%s')]", blockedVehicle).value(hasSize(1)))
                .andExpect(jsonPath("$[?(@.vehicleId == '%s')].status", blockedVehicle).value(contains("ENABLED")));
    }

    @Test
    void filtersTheCurrentAuthorizationsByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/operational-authorizations/current").header("Authorization", supervisorToken)
                        .param("status", "NOT_ENABLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].vehicleId").value(hasItem(blockedVehicle.toString())))
                .andExpect(jsonPath("$[*].status").value(everyItem(is("NOT_ENABLED"))));

        mockMvc.perform(get("/api/v1/operational-authorizations/current").header("Authorization", supervisorToken)
                        .param("status", "ENABLED"))
                .andExpect(jsonPath("$[*].vehicleId").value(not(hasItem(blockedVehicle.toString()))));
    }

    @Test
    void rejectsAnUnknownStatus() throws Exception {
        mockMvc.perform(get("/api/v1/operational-authorizations/current").header("Authorization", supervisorToken)
                        .param("status", "BROKEN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aDriverCannotListTheStatusOfEveryVehicle() throws Exception {
        mockMvc.perform(get("/api/v1/operational-authorizations/current").header("Authorization", driverToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void anyAuthenticatedUserSeesTheStatusOfAVehicle() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/{id}/status", blockedVehicle).header("Authorization", driverToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_ENABLED"));
    }

    @Test
    void reportsAVehicleWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/{id}/status", UUID.randomUUID()).header("Authorization", driverToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("The vehicle has no operational authorization yet."));
    }

    @Test
    void aSupervisorLiftsTheBlockAndIsRecordedAsResponsible() throws Exception {
        mockMvc.perform(post("/api/v1/vehicles/{id}/block-lifts", blockedVehicle).header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\": \"Urgent delivery\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ENABLED"))
                .andExpect(jsonPath("$.isOverride").value(true))
                .andExpect(jsonPath("$.overrideReason").value("Urgent delivery"))
                .andExpect(jsonPath("$.authorizedBy").value(supervisor.getId().toString()));

        mockMvc.perform(get("/api/v1/vehicles/{id}/authorizations", blockedVehicle).header("Authorization", supervisorToken))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].isOverride").value(true));

        var now = Instant.now();
        mockMvc.perform(get("/api/v1/operational-authorizations/overrides").header("Authorization", supervisorToken)
                        .param("from", now.minus(1, ChronoUnit.HOURS).toString()).param("to", now.plus(1, ChronoUnit.HOURS).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].vehicleId").value(hasItem(blockedVehicle.toString())));

        mockMvc.perform(post("/api/v1/vehicles/{id}/block-lifts", blockedVehicle).header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\": \"Again\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void aDriverCannotLiftABlock() throws Exception {
        mockMvc.perform(post("/api/v1/vehicles/{id}/block-lifts", blockedVehicle).header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\": \"I need to leave\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void requiresAReasonToLiftABlock() throws Exception {
        mockMvc.perform(post("/api/v1/vehicles/{id}/block-lifts", blockedVehicle).header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\": \" \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAPeriodThatEndsBeforeItStarts() throws Exception {
        mockMvc.perform(get("/api/v1/operational-authorizations/overrides").header("Authorization", supervisorToken)
                        .param("from", "2026-10-31T00:00:00Z").param("to", "2026-10-01T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("The start of the period must be before its end."));
    }
}
