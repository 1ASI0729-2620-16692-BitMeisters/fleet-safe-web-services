package com.bitmeisters.fleetsafe.platform.evaluation;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionItemCommandService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CreateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InspectionEvaluationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private InspectionItemCommandService inspectionItemCommandService;

    @Autowired
    private EvaluationRuleCommandService evaluationRuleCommandService;

    private String driverToken;
    private String supervisorToken;
    private UUID brakesItemId;
    private final UUID vehicleId = UUID.randomUUID();

    private String tokenFor(Role role) {
        User user = userCommandService.handle(new RegisterUserCommand(new CompanyId(UUID.randomUUID()),
                new EmailAddress(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@andinos.pe"), "S3cure!pass",
                new PersonName("Rosa", "Quispe"), role)).success().orElseThrow();
        return "Bearer " + tokenService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        driverToken = tokenFor(Role.DRIVER);
        supervisorToken = tokenFor(Role.FLEET_SUPERVISOR);
        brakesItemId = inspectionItemCommandService.handle(new CreateInspectionItemCommand(new ItemCode("BRK-" + UUID.randomUUID().toString().substring(0, 6)),
                new ItemName("Service brakes"), "Pedal travel and response.", ItemCategory.SAFETY_COMPONENT, ItemSystem.BRAKES,
                true, false, 1)).success().orElseThrow().getId().value();
        evaluationRuleCommandService.handle(new DefineEvaluationRuleCommand(new InspectionItemId(brakesItemId),
                new RuleName("Failed brakes block the vehicle"), ResultValue.FAIL, RuleImpact.BLOCKING, null));
    }

    private String completeAnInspectionWithFailedBrakes() throws Exception {
        var inspection = mockMvc.perform(post("/api/v1/inspections").header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"vehicleId\": \"" + vehicleId + "\", \"odometer\": 98210}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String inspectionId = JsonPath.read(inspection, "$.id");
        var result = mockMvc.perform(post("/api/v1/inspections/{id}/results", inspectionId).header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"inspectionItemId\": \"" + brakesItemId + "\", \"result\": \"FAIL\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String resultId = JsonPath.read(result, "$.id");
        mockMvc.perform(post("/api/v1/inspections/{id}/observations", inspectionId)
                        .header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resultId\": \"" + resultId + "\", \"description\": \"The brakes do not respond.\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(patch("/api/v1/inspections/{id}", inspectionId).header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk());
        return inspectionId;
    }

    @Test
    void aCompletedInspectionIsEvaluatedAndBlocksTheVehicle() throws Exception {
        completeAnInspectionWithFailedBrakes();

        mockMvc.perform(get("/api/v1/operational-statuses/{vehicleId}", vehicleId).header("Authorization", supervisorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_ENABLED"));
    }

    @Test
    void anInspectionIsEvaluatedOnlyOnce() throws Exception {
        var inspectionId = completeAnInspectionWithFailedBrakes();

        mockMvc.perform(post("/api/v1/evaluations").header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"inspectionId\": \"" + inspectionId + "\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void reportsAnInspectionThatIsNotCompleted() throws Exception {
        mockMvc.perform(post("/api/v1/evaluations").header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"inspectionId\": \"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("There is no completed inspection with this identifier."));
    }

    @Test
    void aDriverCannotEvaluateAnInspection() throws Exception {
        mockMvc.perform(post("/api/v1/evaluations").header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"inspectionId\": \"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isForbidden());
    }
}
