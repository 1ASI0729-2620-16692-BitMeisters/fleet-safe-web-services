package com.bitmeisters.fleetsafe.platform.incident;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.shared.application.internal.outboundservices.storage.FileStorageService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IncidentFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private FileStorageService fileStorageService;

    private static final String STORED_EVIDENCE = "https://fleetsafe.blob.core.windows.net/evidences/brakes.jpg";

    private String administratorToken;
    private String supervisorToken;
    private String driverToken;
    private UUID supervisorId;
    private final UUID vehicleId = UUID.randomUUID();

    private User register(Role role) {
        return userCommandService.handle(new RegisterUserCommand(new CompanyId(UUID.randomUUID()),
                new EmailAddress(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@andinos.pe"), "S3cure!pass",
                new PersonName("Ana", "Quispe"), role)).success().orElseThrow();
    }

    @BeforeEach
    void setUp() {
        administratorToken = "Bearer " + tokenService.generateToken(register(Role.ADMINISTRATOR));
        var supervisor = register(Role.FLEET_SUPERVISOR);
        supervisorId = supervisor.getId();
        supervisorToken = "Bearer " + tokenService.generateToken(supervisor);
        driverToken = "Bearer " + tokenService.generateToken(register(Role.DRIVER));
        when(fileStorageService.store(any(), any())).thenReturn(Optional.of(STORED_EVIDENCE));
        when(fileStorageService.temporaryReadUrl(any())).thenAnswer(invocation -> invocation.getArgument(0) == null ? null : invocation.getArgument(0) + "?sig=read");
    }

    private String registerIncidentType() throws Exception {
        var response = mockMvc.perform(post("/api/v1/incident-types").header("Authorization", administratorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"MEC-" + UUID.randomUUID().toString().substring(0, 6) + "\", \"name\": \"Mechanical failure\", \"description\": \"A part of the vehicle stopped working.\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    private String reportIncident(String incidentTypeId) throws Exception {
        var response = mockMvc.perform(post("/api/v1/incidents").header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicleId\": \"" + vehicleId + "\", \"incidentTypeId\": \"" + incidentTypeId
                                + "\", \"origin\": \"OPERATION\", \"description\": \"The brakes make noise.\", \"severity\": \"HIGH\", \"reportedBy\": \""
                                + supervisorId + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    @Test
    void aSupervisorFollowsAnIncidentUntilItIsResolved() throws Exception {
        var incidentId = reportIncident(registerIncidentType());
        Instant baseTime = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS);

        // Move the incident from REGISTERED to IN_REVIEW.
        mockMvc.perform(patch("/api/v1/incidents/{id}", incidentId)
                        .header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"IN_REVIEW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_REVIEW"));

        var action = mockMvc.perform(post("/api/v1/incidents/{id}/corrective-actions", incidentId)
                        .header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"incidentId\": \"" + incidentId + "\", \"description\": \"Brake pads replaced.\", \"performedBy\": \""
                                + supervisorId + "\", \"performedAt\": \"" + baseTime.plusSeconds(1800) + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.performedByName").value("Ana Quispe"))
                .andReturn().getResponse().getContentAsString();
        String actionId = JsonPath.read(action, "$.id");

        mockMvc.perform(multipart("/api/v1/incidents/{incidentId}/evidences", incidentId)
                        .file(new MockMultipartFile("file", "brakes.jpg", "image/jpeg", new byte[] {1, 2, 3}))
                        .param("correctiveActionId", actionId)
                        .header("Authorization", supervisorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evidenceUrl").value(STORED_EVIDENCE + "?sig=read"));

        var repair = mockMvc.perform(post("/api/v1/incidents/{id}/repairs", incidentId)
                        .header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"incidentId\": \"" + incidentId + "\", \"workshop\": \"Taller Norte\", \"cost\": 350.00, \"startedAt\": \""
                                + baseTime.plusSeconds(3600) + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String repairId = JsonPath.read(repair, "$.id");

        mockMvc.perform(patch("/api/v1/incidents/{incidentId}/repairs/{repairId}", incidentId, repairId)
                        .header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"COMPLETED\", \"finishedAt\": \""
                                + baseTime.plusSeconds(14400) + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/incidents/{id}/follow-ups", incidentId)
                        .header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"incidentId\": \"" + incidentId + "\", \"note\": \"Tested on the yard.\", \"createdBy\": \""
                                + supervisorId + "\", \"createdAt\": \"" + baseTime.plusSeconds(18000) + "\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/v1/incidents/{id}", incidentId)
                        .header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"RESOLVED\", \"resolutionType\": \"REPAIRED\", \"resolvedAt\": \""
                                + baseTime.plusSeconds(21600) + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolutionType").value("REPAIRED"));

        mockMvc.perform(patch("/api/v1/incidents/{id}", incidentId)
                        .header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"RESOLVED\", \"resolutionType\": \"REPAIRED\", \"resolvedAt\": \""
                                + baseTime.plusSeconds(25200) + "\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/incidents/{id}", incidentId)
                        .header("Authorization", driverToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportedByName").value("Ana Quispe"))
                .andExpect(jsonPath("$.correctiveActions[0].evidenceUrl").value(STORED_EVIDENCE + "?sig=read"))
                .andExpect(jsonPath("$.followUps[0].createdByName").value("Ana Quispe"));
    }

    @Test
    void reportsAnUnknownIncidentTypeInTheLanguageOfTheRequest() throws Exception {
        mockMvc.perform(post("/api/v1/incidents").header("Authorization", supervisorToken).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicleId\": \"" + vehicleId + "\", \"incidentTypeId\": \"" + UUID.randomUUID()
                                + "\", \"origin\": \"OPERATION\", \"description\": \"Flat tire.\", \"severity\": \"LOW\", \"reportedBy\": \""
                                + supervisorId + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("El tipo de incidencia no existe."));
    }

    @Test
    void onlyAnAdministratorDefinesIncidentTypes() throws Exception {
        mockMvc.perform(post("/api/v1/incident-types").header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"ELE-01\", \"name\": \"Electrical failure\", \"description\": \"Lights or battery.\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void aDriverCannotResolveAnIncident() throws Exception {
        var incidentId = reportIncident(registerIncidentType());

        mockMvc.perform(patch("/api/v1/incidents/{id}", incidentId)
                        .header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"RESOLVED\", \"resolutionType\": \"REPAIRED\", \"resolvedAt\": \"2026-11-08T17:00:00Z\"}"))
                .andExpect(status().isForbidden());
    }
}
