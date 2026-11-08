package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EvaluationRulesControllerIntegrationTest {

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
        User user = userCommandService.handle(new RegisterUserCommand(new CompanyId(UUID.randomUUID()),
                new EmailAddress(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@andinos.pe"), "S3cure!pass",
                new PersonName("Ana", "Quispe"), role)).success().orElseThrow();
        return "Bearer " + tokenService.generateToken(user);
    }

    @BeforeEach
    void signIn() {
        administrator = bearer(Role.ADMINISTRATOR);
        supervisor = bearer(Role.FLEET_SUPERVISOR);
        driver = bearer(Role.DRIVER);
    }

    private String defineBrakesRule() throws Exception {
        var body = """
                {"inspectionItemId": "%s", "name": "Brake failure blocks the vehicle", "foundResult": "FAIL", "resultingImpact": "BLOCKING"}
                """.formatted(UUID.randomUUID());
        var response = mockMvc.perform(post("/api/v1/evaluation-rules").header("Authorization", administrator)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultingImpact").value("BLOCKING"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void anAdministratorDefinesAndDeactivatesARule() throws Exception {
        var id = defineBrakesRule();

        mockMvc.perform(post("/api/v1/evaluation-rules/{id}/deactivation", id).header("Authorization", administrator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        mockMvc.perform(post("/api/v1/evaluation-rules/{id}/deactivation", id).header("Authorization", administrator))
                .andExpect(status().isConflict());
    }

    @Test
    void aSupervisorReadsTheRulesButCannotDefineThem() throws Exception {
        var id = defineBrakesRule();

        mockMvc.perform(get("/api/v1/evaluation-rules/{id}", id).header("Authorization", supervisor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Brake failure blocks the vehicle"));
        mockMvc.perform(get("/api/v1/evaluation-rules").header("Authorization", supervisor))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/evaluation-rules").header("Authorization", supervisor).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inspectionItemId": "%s", "name": "Rule", "foundResult": "FAIL", "resultingImpact": "BLOCKING"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isForbidden());
    }

    @Test
    void aDriverCannotReadTheRules() throws Exception {
        mockMvc.perform(get("/api/v1/evaluation-rules").header("Authorization", driver))
                .andExpect(status().isForbidden());
    }

    @Test
    void reportsARuleThatDoesNotExistInTheLanguageOfTheRequest() throws Exception {
        mockMvc.perform(get("/api/v1/evaluation-rules/{id}", UUID.randomUUID()).header("Authorization", supervisor).header("Accept-Language", "es-419"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No se encontró la regla de evaluación."));
    }

    @Test
    void rejectsAnUnknownImpact() throws Exception {
        mockMvc.perform(post("/api/v1/evaluation-rules").header("Authorization", administrator).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inspectionItemId": "%s", "name": "Rule", "foundResult": "FAIL", "resultingImpact": "CATASTROPHIC"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest());
    }
}
