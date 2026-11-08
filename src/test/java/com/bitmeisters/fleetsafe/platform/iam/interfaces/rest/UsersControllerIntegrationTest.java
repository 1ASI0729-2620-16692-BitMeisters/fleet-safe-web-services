package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest;

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

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    private final CompanyId andinos = new CompanyId(UUID.randomUUID());
    private User administrator;
    private User supervisor;
    private User otherCompanyDriver;

    private User register(CompanyId company, String email, String lastName, Role role) {
        return userCommandService.handle(new RegisterUserCommand(company, new EmailAddress(email), "S3cure!pass",
                new PersonName("Ana", lastName), role)).success().orElseThrow();
    }

    private String bearer(User user) {
        return "Bearer " + tokenService.generateToken(user);
    }

    @BeforeEach
    void registerTheTeam() {
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        administrator = register(andinos, "admin-" + suffix + "@andinos.pe", "Zavala", Role.ADMINISTRATOR);
        supervisor = register(andinos, "supervisor-" + suffix + "@andinos.pe", "Quispe", Role.FLEET_SUPERVISOR);
        otherCompanyDriver = register(new CompanyId(UUID.randomUUID()), "driver-" + suffix + "@otra.pe", "Huamán", Role.DRIVER);
    }

    @Test
    void listsOnlyTheUsersOfTheCompanyOfTheAdministrator() throws Exception {
        mockMvc.perform(get("/api/v1/users").header("Authorization", bearer(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].lastName").value(containsInAnyOrder("Quispe", "Zavala")))
                .andExpect(jsonPath("$.content[0].lastName").value("Quispe"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void filtersPagesAndSortsTheUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("role", "FLEET_SUPERVISOR").header("Authorization", bearer(administrator)))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].role").value("FLEET_SUPERVISOR"));

        mockMvc.perform(get("/api/v1/users").param("size", "1").param("page", "1").param("sort", "lastName,desc")
                        .header("Authorization", bearer(administrator)))
                .andExpect(jsonPath("$.content[0].lastName").value("Quispe"))
                .andExpect(jsonPath("$.page.totalPages").value(2));
    }

    @Test
    void forbidsTheListToANonAdministrator() throws Exception {
        mockMvc.perform(get("/api/v1/users").header("Authorization", bearer(supervisor)).header("Accept-Language", "es-419"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Tu rol no permite realizar esta operación."));
    }

    @Test
    void registersAUserInTheCompanyOfTheAdministrator() throws Exception {
        var body = """
                {"email": "Nuevo.Conductor@Andinos.pe", "password": "S3cure!pass", "firstName": "Luis", "lastName": "Huamán", "role": "DRIVER"}
                """;

        mockMvc.perform(post("/api/v1/users").header("Authorization", bearer(administrator)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("nuevo.conductor@andinos.pe"))
                .andExpect(jsonPath("$.companyId").value(andinos.value().toString()))
                .andExpect(jsonPath("$.isActive").value(true));

        mockMvc.perform(post("/api/v1/users").header("Authorization", bearer(administrator)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsAnUnknownRole() throws Exception {
        mockMvc.perform(post("/api/v1/users").header("Authorization", bearer(administrator)).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "x@andinos.pe", "password": "S3cure!pass", "firstName": "X", "lastName": "Y", "role": "OWNER"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void letsAUserReadTheirOwnAccountButNotAnotherOne() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", supervisor.getId()).header("Authorization", bearer(supervisor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(supervisor.getId().toString()));

        mockMvc.perform(get("/api/v1/users/{id}", administrator.getId()).header("Authorization", bearer(supervisor)))
                .andExpect(status().isForbidden());
    }

    @Test
    void hidesTheUsersOfAnotherCompany() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", otherCompanyDriver.getId()).header("Authorization", bearer(administrator)))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/v1/users/{id}", otherCompanyDriver.getId()).header("Authorization", bearer(administrator))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"role\": \"ADMINISTRATOR\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changesOnlyTheFieldsSent() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}", supervisor.getId()).header("Authorization", bearer(administrator))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"lastName\": \"Quispe Mamani\", \"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Quispe Mamani"))
                .andExpect(jsonPath("$.role").value("FLEET_SUPERVISOR"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void rejectsAnEmptyChange() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}", supervisor.getId()).header("Authorization", bearer(administrator))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Send at least one field to change."));
    }

    @Test
    void rejectsAPageSizeOutOfRange() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("size", "0").header("Authorization", bearer(administrator)))
                .andExpect(status().isBadRequest());
    }
}
