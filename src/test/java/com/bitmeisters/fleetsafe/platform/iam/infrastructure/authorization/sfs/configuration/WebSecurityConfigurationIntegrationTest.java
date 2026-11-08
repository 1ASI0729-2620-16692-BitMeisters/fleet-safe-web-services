package com.bitmeisters.fleetsafe.platform.iam.infrastructure.authorization.sfs.configuration;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.DeactivateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(WebSecurityConfigurationIntegrationTest.WhoAmIController.class)
class WebSecurityConfigurationIntegrationTest {

    @TestConfiguration
    @RestController
    static class WhoAmIController {
        @GetMapping("/api/v1/who-am-i")
        String whoAmI(@AuthenticationPrincipal UserDetailsImpl user) {
            return user.getId() + " " + user.getAuthorities().iterator().next().getAuthority();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private TokenService tokenService;

    private User registerSupervisor() {
        return userCommandService.handle(new RegisterUserCommand(new CompanyId(UUID.randomUUID()),
                new EmailAddress("supervisor-" + UUID.randomUUID() + "@andinos.pe"), "S3cure!pass",
                new PersonName("Rosa", "Quispe"), Role.FLEET_SUPERVISOR)).success().orElseThrow();
    }

    @Test
    void rejectsARequestWithoutTokenWithATranslatedProblem() throws Exception {
        mockMvc.perform(get("/api/v1/who-am-i"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("You need to sign in to access this resource."));

        mockMvc.perform(get("/api/v1/who-am-i").header("Accept-Language", "es-419"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Debes iniciar sesión para acceder a este recurso."));
    }

    @Test
    void identifiesTheUserOfAValidToken() throws Exception {
        var user = registerSupervisor();

        mockMvc.perform(get("/api/v1/who-am-i").header("Authorization", "Bearer " + tokenService.generateToken(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(user.getId() + " ROLE_FLEET_SUPERVISOR"));
    }

    @Test
    void rejectsAnAlteredToken() throws Exception {
        var token = tokenService.generateToken(registerSupervisor());

        mockMvc.perform(get("/api/v1/who-am-i").header("Authorization", "Bearer " + token.substring(0, token.length() - 3) + "abc"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsTheTokenOfAUserDeactivatedAfterSigningIn() throws Exception {
        var user = registerSupervisor();
        var token = tokenService.generateToken(user);
        userCommandService.handle(new DeactivateUserCommand(user.getId()));

        mockMvc.perform(get("/api/v1/who-am-i").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void keepsTheApiDocumentationOpen() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }
}
