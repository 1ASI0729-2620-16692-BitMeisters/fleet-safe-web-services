package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.DeactivateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TokensControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User register(String email) {
        return userCommandService.handle(new RegisterUserCommand(new CompanyId(UUID.randomUUID()), new EmailAddress(email),
                "S3cure!pass", new PersonName("Rosa", "Quispe"), Role.FLEET_SUPERVISOR)).success().orElseThrow();
    }

    private String body(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(Map.of("email", email, "password", password));
    }

    @Test
    void returnsATokenAndTheUserForTheRightPassword() throws Exception {
        var user = register("rosa.login@andinos.pe");

        var response = mockMvc.perform(post("/api/v1/tokens").contentType(MediaType.APPLICATION_JSON)
                        .content(body("Rosa.Login@Andinos.pe", "S3cure!pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400))
                .andExpect(jsonPath("$.user.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.user.email").value("rosa.login@andinos.pe"))
                .andExpect(jsonPath("$.user.role").value("FLEET_SUPERVISOR"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        var token = objectMapper.readTree(response).get("accessToken").asText();
        assertThat(tokenService.getUserIdFromToken(token)).isEqualTo(user.getId().toString());
    }

    @Test
    void rejectsAWrongPasswordWithATranslatedMessage() throws Exception {
        register("rosa.wrong@andinos.pe");

        mockMvc.perform(post("/api/v1/tokens").contentType(MediaType.APPLICATION_JSON).header("Accept-Language", "es-419")
                        .content(body("rosa.wrong@andinos.pe", "Wrong!pass")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("El correo electrónico o la contraseña no son correctos."));
    }

    @Test
    void rejectsAnInactiveUser() throws Exception {
        var user = register("rosa.inactive@andinos.pe");
        userCommandService.handle(new DeactivateUserCommand(user.getId()));

        mockMvc.perform(post("/api/v1/tokens").contentType(MediaType.APPLICATION_JSON)
                        .content(body("rosa.inactive@andinos.pe", "S3cure!pass")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("This user is inactive and cannot sign in."));
    }

    @Test
    void rejectsARequestWithoutPassword() throws Exception {
        mockMvc.perform(post("/api/v1/tokens").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"rosa@andinos.pe\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAnEmailWithoutAValidFormat() throws Exception {
        mockMvc.perform(post("/api/v1/tokens").contentType(MediaType.APPLICATION_JSON)
                        .content(body("not-an-email", "S3cure!pass")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("The email does not have a valid format."));
    }
}
