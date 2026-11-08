package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionItemCommandService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.outboundservices.storage.EvidenceStorageService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CreateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class InspectionsControllerIntegrationTest {

    private static final FileUrl STORED_PHOTO = new FileUrl("https://fleetsafeevidences.blob.core.windows.net/evidences/photo.png");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private InspectionItemCommandService inspectionItemCommandService;

    @MockitoBean
    private EvidenceStorageService evidenceStorageService;

    private String driverToken;
    private String otherDriverToken;
    private String supervisorToken;
    private UUID brakesItemId;
    private final UUID vehicleId = UUID.randomUUID();

    private String tokenFor(Role role) {
        User user = userCommandService.handle(new RegisterUserCommand(new CompanyId(UUID.randomUUID()),
                new EmailAddress(role.name().toLowerCase() + "-" + UUID.randomUUID() + "@andinos.pe"), "S3cure!pass",
                new PersonName("Luis", "Mamani"), role)).success().orElseThrow();
        return "Bearer " + tokenService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        driverToken = tokenFor(Role.DRIVER);
        otherDriverToken = tokenFor(Role.DRIVER);
        supervisorToken = tokenFor(Role.FLEET_SUPERVISOR);
        brakesItemId = inspectionItemCommandService.handle(new CreateInspectionItemCommand(new ItemCode("BRK-" + UUID.randomUUID().toString().substring(0, 6)),
                new ItemName("Service brakes"), "Pedal travel and response.", ItemCategory.SAFETY_COMPONENT, ItemSystem.BRAKES,
                true, false, 1)).success().orElseThrow().getId().value();
        when(evidenceStorageService.store(any(), any())).thenReturn(Optional.of(STORED_PHOTO));
        when(evidenceStorageService.temporaryReadUrl(any())).thenAnswer(invocation -> invocation.<FileUrl>getArgument(0).value() + "?sig=read");
    }

    private String startInspection() throws Exception {
        var response = mockMvc.perform(post("/api/v1/inspections").header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"vehicleId\": \"" + vehicleId + "\", \"odometer\": 125430}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    private String registerObservedBrakes(String inspectionId) throws Exception {
        var response = mockMvc.perform(post("/api/v1/inspections/{id}/results", inspectionId).header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"inspectionItemId\": \"" + brakesItemId + "\", \"result\": \"OBSERVED\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName").value("Service brakes"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    private String addObservation(String inspectionId, String resultId) throws Exception {
        var response = mockMvc.perform(post("/api/v1/inspections/{id}/results/{resultId}/observations", inspectionId, resultId)
                        .header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"description\": \"The pedal feels soft.\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    @Test
    void aDriverPerformsAndCompletesAnInspectionWithAPhoto() throws Exception {
        var inspectionId = startInspection();
        var resultId = registerObservedBrakes(inspectionId);
        var observationId = addObservation(inspectionId, resultId);

        mockMvc.perform(multipart("/api/v1/inspections/{id}/results/{resultId}/observations/{observationId}/evidences",
                        inspectionId, resultId, observationId)
                        .file(new MockMultipartFile("file", "brakes.png", "image/png", new byte[] {1, 2, 3}))
                        .header("Authorization", driverToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mediaType").value("image/png"))
                .andExpect(jsonPath("$.fileUrl").value(endsWith("?sig=read")));

        mockMvc.perform(patch("/api/v1/inspections/{id}", inspectionId).header("Authorization", driverToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.results[0].observations[0].evidences[0].mediaType").value("image/png"));
    }

    @Test
    void onlyTheDriverWhoPerformedItSeesTheInspection() throws Exception {
        var inspectionId = startInspection();

        mockMvc.perform(get("/api/v1/inspections/{id}", inspectionId).header("Authorization", otherDriverToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/inspections").header("Authorization", otherDriverToken))
                .andExpect(jsonPath("$[*].id").value(not(hasItem(inspectionId))));
        mockMvc.perform(get("/api/v1/inspections").header("Authorization", supervisorToken).param("vehicleId", vehicleId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(inspectionId)));
    }

    @Test
    void answersServiceUnavailableWhenThePhotoCannotBeStored() throws Exception {
        when(evidenceStorageService.store(eq("image/png"), any())).thenReturn(Optional.empty());
        var inspectionId = startInspection();
        var resultId = registerObservedBrakes(inspectionId);
        var observationId = addObservation(inspectionId, resultId);

        mockMvc.perform(multipart("/api/v1/inspections/{id}/results/{resultId}/observations/{observationId}/evidences",
                        inspectionId, resultId, observationId)
                        .file(new MockMultipartFile("file", "brakes.png", "image/png", new byte[] {1}))
                        .header("Authorization", driverToken))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("The photo could not be stored. Try again later."));
    }

    @Test
    void rejectsAFileThatIsNotAPhoto() throws Exception {
        var inspectionId = startInspection();
        var resultId = registerObservedBrakes(inspectionId);
        var observationId = addObservation(inspectionId, resultId);

        mockMvc.perform(multipart("/api/v1/inspections/{id}/results/{resultId}/observations/{observationId}/evidences",
                        inspectionId, resultId, observationId)
                        .file(new MockMultipartFile("file", "notes.pdf", "application/pdf", new byte[] {1}))
                        .header("Authorization", driverToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aSupervisorCannotStartAnInspection() throws Exception {
        mockMvc.perform(post("/api/v1/inspections").header("Authorization", supervisorToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"vehicleId\": \"" + vehicleId + "\", \"odometer\": 10}"))
                .andExpect(status().isForbidden());
    }
}
