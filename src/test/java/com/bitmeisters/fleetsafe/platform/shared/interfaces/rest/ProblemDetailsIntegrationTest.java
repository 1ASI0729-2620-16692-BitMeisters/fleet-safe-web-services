package com.bitmeisters.fleetsafe.platform.shared.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ProblemDetailsIntegrationTest.ProbeController.class)
class ProblemDetailsIntegrationTest {

    record Probe(String name) {
    }

    @TestConfiguration
    @RestController
    static class ProbeController {
        @PostMapping("/api/v1/probes")
        Probe create(@RequestBody Probe probe) {
            return probe;
        }

        @GetMapping("/api/v1/probes/{probeId}")
        String find(@PathVariable UUID probeId) {
            return probeId.toString();
        }

        @GetMapping("/api/v1/probes/failure")
        String fail() {
            throw new IllegalStateException("database is down at 10.0.0.5");
        }

        @GetMapping("/api/v1/probes/administration")
        @PreAuthorize("hasRole('ADMINISTRATOR')")
        String administer() {
            return "done";
        }
    }

    @Autowired
    private MockMvc mockMvc;

    private RequestPostProcessor supervisor() {
        return user("supervisor").roles("FLEET_SUPERVISOR");
    }

    @Test
    void answersAMalformedBodyWithATranslatedProblem() throws Exception {
        mockMvc.perform(post("/api/v1/probes").with(supervisor())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\": "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("The request body is missing or is not valid JSON."));

        mockMvc.perform(post("/api/v1/probes").with(supervisor()).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\": "))
                .andExpect(jsonPath("$.detail").value("El cuerpo de la solicitud falta o no es un JSON válido."));
    }

    @Test
    void answersAnInvalidIdentifierWithATranslatedProblem() throws Exception {
        mockMvc.perform(get("/api/v1/probes/abc").with(supervisor()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("The value 'abc' is not valid for 'probeId'."));

        mockMvc.perform(get("/api/v1/probes/abc").with(supervisor()).header("Accept-Language", "es-419"))
                .andExpect(jsonPath("$.detail").value("El valor 'abc' no es válido para 'probeId'."));
    }

    @Test
    void answersAnUnknownRouteWithATranslatedProblem() throws Exception {
        mockMvc.perform(get("/api/v1/nothing-here").with(supervisor()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("The requested resource does not exist."));

        mockMvc.perform(get("/api/v1/nothing-here").with(supervisor()).header("Accept-Language", "es-419"))
                .andExpect(jsonPath("$.detail").value("El recurso solicitado no existe."));
    }

    @Test
    void answersAnUnsupportedMethodWithATranslatedProblem() throws Exception {
        mockMvc.perform(post("/api/v1/probes/failure").with(supervisor()))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("The method POST is not supported by this resource."));
    }

    @Test
    void answersAnUnexpectedErrorWithoutItsInternalDetails() throws Exception {
        mockMvc.perform(get("/api/v1/probes/failure").with(supervisor()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Something went wrong on our side. Try again later."));

        mockMvc.perform(get("/api/v1/probes/failure").with(supervisor()).header("Accept-Language", "es-419"))
                .andExpect(jsonPath("$.detail").value("Algo salió mal de nuestro lado. Inténtalo más tarde."));
    }

    @Test
    void keepsRejectingAForbiddenRoleWithForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/probes/administration").with(supervisor()))
                .andExpect(status().isForbidden());
    }
}
