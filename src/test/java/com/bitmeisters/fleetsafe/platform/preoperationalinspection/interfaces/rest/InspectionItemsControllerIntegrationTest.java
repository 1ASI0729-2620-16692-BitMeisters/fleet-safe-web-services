package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InspectionItemsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String administrator;
    private String supervisor;
    private String driver;

    private String bearer(Role role) {
        User user = userCommandService.handle(new RegisterUserCommand(
                new CompanyId(UUID.randomUUID()),
                new EmailAddress(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@bitmeisters.pe"),
                "S3cure!pass",
                new PersonName("Carlos", "Mendoza"),
                role)).success().orElseThrow();
        return "Bearer " + tokenService.generateToken(user);
    }

    @BeforeEach
    void signIn() {
        administrator = bearer(Role.ADMINISTRATOR);
        supervisor = bearer(Role.FLEET_SUPERVISOR);
        driver = bearer(Role.DRIVER);
    }

    private String createItem(String code, String name, String category, String system, boolean safety, boolean evidence, int order) throws Exception {
        var body = """
                {
                    "code": "%s",
                    "name": "%s",
                    "description": "Inspection criteria instructions",
                    "category": "%s",
                    "system": "%s",
                    "isSafetyComponent": %s,
                    "requiresEvidence": %s,
                    "displayOrder": %d
                }
                """.formatted(code, name, category, system, safety, evidence, order);

        var response = mockMvc.perform(post("/api/v1/inspection-items")
                        .header("Authorization", administrator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.isActive").value(true))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void anAdministratorCreatesUpdatesAndDeactivatesAnInspectionItem() throws Exception {
        var id = createItem("BRK-99", "Brake fluid level and pressure", "SAFETY_COMPONENT", "BRAKES", true, true, 1);

        // Update item
        var updateBody = """
                {
                    "name": "Updated brake inspection",
                    "description": "Updated criteria",
                    "category": "COMPONENT",
                    "system": "BRAKES",
                    "isSafetyComponent": false,
                    "requiresEvidence": false,
                    "displayOrder": 3
                }
                """;

        mockMvc.perform(put("/api/v1/inspection-items/{id}", id)
                        .header("Authorization", administrator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated brake inspection"))
                .andExpect(jsonPath("$.isSafetyComponent").value(false))
                .andExpect(jsonPath("$.displayOrder").value(3));

        mockMvc.perform(patch("/api/v1/inspection-items/{id}", id)
                        .header("Authorization", administrator)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        mockMvc.perform(patch("/api/v1/inspection-items/{id}", id)
                        .header("Authorization", administrator)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"isActive\": false}"))
                .andExpect(status().isConflict());
    }

    @Test
    void anyAuthenticatedUserReadsItemsAndFilters() throws Exception {
        var id = createItem("LGT-01", "Headlights and turn signals", "COMPONENT", "LIGHTS", false, false, 2);

        // Driver reads item by id
        mockMvc.perform(get("/api/v1/inspection-items/{id}", id)
                        .header("Authorization", driver))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("LGT-01"));

        // Supervisor reads catalog with filters
        mockMvc.perform(get("/api/v1/inspection-items")
                        .param("system", "LIGHTS")
                        .param("active", "true")
                        .header("Authorization", supervisor))
                .andExpect(status().isOk());
    }

    @Test
    void aDriverCannotCreateOrUpdateAnItem() throws Exception {
        var body = """
                {
                    "code": "DRV-01",
                    "name": "Unauthorized create",
                    "category": "COMPONENT",
                    "system": "CABIN",
                    "isSafetyComponent": false,
                    "requiresEvidence": false,
                    "displayOrder": 1
                }
                """;

        mockMvc.perform(post("/api/v1/inspection-items")
                        .header("Authorization", driver)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void reportsAnItemThatDoesNotExistInTheLanguageOfTheRequest() throws Exception {
        mockMvc.perform(get("/api/v1/inspection-items/{id}", UUID.randomUUID())
                        .header("Authorization", driver)
                        .header("Accept-Language", "es-419"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No se encontró el elemento de inspección."));
    }

    @Test
    void rejectsDuplicateCode() throws Exception {
        createItem("DUP-01", "First item", "COMPONENT", "CABIN", false, false, 1);

        var duplicateBody = """
                {
                    "code": "DUP-01",
                    "name": "Duplicate item",
                    "category": "COMPONENT",
                    "system": "CABIN",
                    "isSafetyComponent": false,
                    "requiresEvidence": false,
                    "displayOrder": 2
                }
                """;

        mockMvc.perform(post("/api/v1/inspection-items")
                        .header("Authorization", administrator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateBody))
                .andExpect(status().isConflict());
    }
}
