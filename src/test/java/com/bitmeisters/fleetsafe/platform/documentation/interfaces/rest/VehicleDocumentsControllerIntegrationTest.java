package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.documentation.application.internal.outboundservices.calendar.DocumentationCalendar;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleDocumentsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DocumentationCalendar documentationCalendar;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String administrator;
    private String supervisor;
    private String driver;
    private LocalDate today;
    private UUID truck;

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
        today = documentationCalendar.today();
        truck = UUID.randomUUID();
    }

    private String uniqueCode() {
        return "SOAT-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String registerDocumentType(String code) throws Exception {
        var response = mockMvc.perform(post("/api/v1/document-types").header("Authorization", administrator)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "%s", "name": "Mandatory traffic accident insurance", "isRequired": true}
                                """.formatted(code)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isRequired").value(true))
                .andExpect(jsonPath("$.isActive").value(true))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String document(String documentTypeId, String number, LocalDate expirationDate) {
        return """
                {"documentTypeId": "%s", "number": "%s", "issueDate": "%s", "expirationDate": "%s"}
                """.formatted(documentTypeId, number, expirationDate.minusYears(1), expirationDate);
    }

    private String registerDocument(String documentTypeId, String number, LocalDate expirationDate) throws Exception {
        var response = mockMvc.perform(post("/api/v1/vehicles/{vehicleId}/documents", truck).header("Authorization", supervisor)
                        .contentType(MediaType.APPLICATION_JSON).content(document(documentTypeId, number, expirationDate)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void aSupervisorRegistersADocumentAndSeesItsStatus() throws Exception {
        var soat = registerDocumentType(uniqueCode());

        mockMvc.perform(post("/api/v1/vehicles/{vehicleId}/documents", truck).header("Authorization", supervisor)
                        .contentType(MediaType.APPLICATION_JSON).content(document(soat, "SOAT-2026-004512", today.plusDays(10))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleId").value(truck.toString()))
                .andExpect(jsonPath("$.status").value("EXPIRING"))
                .andExpect(jsonPath("$.expirationDate").value(today.plusDays(10).toString()));

        mockMvc.perform(get("/api/v1/vehicles/{vehicleId}/documents", truck).header("Authorization", driver))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].number").value("SOAT-2026-004512"));
    }

    @Test
    void listsOnlyTheDocumentsAboutToExpire() throws Exception {
        var soat = registerDocumentType(uniqueCode());
        var expiring = registerDocument(soat, "SOAT-EXPIRING", today.plusDays(5));
        var valid = registerDocument(soat, "SOAT-VALID", today.plusDays(90));
        var expired = registerDocument(soat, "SOAT-EXPIRED", today.minusDays(1));

        mockMvc.perform(get("/api/v1/documents/expiring").header("Authorization", supervisor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(expiring)))
                .andExpect(jsonPath("$[*].id").value(not(hasItem(valid))))
                .andExpect(jsonPath("$[*].id").value(not(hasItem(expired))));

        mockMvc.perform(get("/api/v1/documents/expiring").param("days", "120").header("Authorization", supervisor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(valid)));

        mockMvc.perform(get("/api/v1/documents").header("Authorization", supervisor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(expired)));
    }

    @Test
    void renewingADocumentRecalculatesItsStatus() throws Exception {
        var soat = registerDocumentType(uniqueCode());
        var id = registerDocument(soat, "SOAT-OLD", today.minusDays(3));

        mockMvc.perform(put("/api/v1/vehicles/{vehicleId}/documents/{documentId}", truck, id).header("Authorization", supervisor)
                        .contentType(MediaType.APPLICATION_JSON).content(document(soat, "SOAT-NEW", today.plusYears(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("SOAT-NEW"))
                .andExpect(jsonPath("$.status").value("VALID"));

        mockMvc.perform(put("/api/v1/vehicles/{vehicleId}/documents/{documentId}", UUID.randomUUID(), id).header("Authorization", supervisor)
                        .contentType(MediaType.APPLICATION_JSON).content(document(soat, "SOAT-NEW", today.plusYears(1))))
                .andExpect(status().isNotFound());
    }

    @Test
    void aDriverCannotRegisterDocumentsNorTypes() throws Exception {
        var soat = registerDocumentType(uniqueCode());

        mockMvc.perform(post("/api/v1/vehicles/{vehicleId}/documents", truck).header("Authorization", driver)
                        .contentType(MediaType.APPLICATION_JSON).content(document(soat, "SOAT-2026-004512", today.plusDays(10))))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/document-types").header("Authorization", supervisor).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "TP", "name": "Technical inspection", "isRequired": true}
                                """))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/documents/expiring").header("Authorization", driver))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/document-types").header("Authorization", driver))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsADocumentTypeCodeThatAlreadyExists() throws Exception {
        var code = uniqueCode();
        registerDocumentType(code);

        mockMvc.perform(post("/api/v1/document-types").header("Authorization", administrator).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "%s", "name": "Another insurance", "isRequired": false}
                                """.formatted(code.toLowerCase())))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsADocumentOfAnUnknownType() throws Exception {
        mockMvc.perform(post("/api/v1/vehicles/{vehicleId}/documents", truck).header("Authorization", supervisor)
                        .contentType(MediaType.APPLICATION_JSON).content(document(UUID.randomUUID().toString(), "SOAT-1", today.plusDays(10))))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void reportsAnInvertedPeriodInTheLanguageOfTheRequest() throws Exception {
        var soat = registerDocumentType(uniqueCode());

        mockMvc.perform(post("/api/v1/vehicles/{vehicleId}/documents", truck).header("Authorization", supervisor).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"documentTypeId": "%s", "number": "SOAT-1", "issueDate": "%s", "expirationDate": "%s"}
                                """.formatted(soat, today, today.minusDays(1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La fecha de vencimiento no puede ser anterior a la fecha de emisión."));
    }

    @Test
    void rejectsAnExpiringWindowLongerThanAYear() throws Exception {
        mockMvc.perform(get("/api/v1/documents/expiring").param("days", "400").header("Authorization", supervisor))
                .andExpect(status().isBadRequest());
    }
}
