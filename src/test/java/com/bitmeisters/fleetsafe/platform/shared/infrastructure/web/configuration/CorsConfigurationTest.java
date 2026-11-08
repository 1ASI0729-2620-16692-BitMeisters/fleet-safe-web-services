package com.bitmeisters.fleetsafe.platform.shared.infrastructure.web.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(CorsConfigurationTest.PingController.class)
class CorsConfigurationTest {

    @TestConfiguration
    @RestController
    static class PingController {
        @GetMapping("/api/v1/ping")
        String ping() {
            return "pong";
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Value("${documentation.application.version}")
    private String projectVersion;

    @Test
    void allowsTheDeployedWebApplication() throws Exception {
        mockMvc.perform(options("/api/v1/ping")
                        .header("Origin", "https://1asi0729-2620-16692-bitmeisters.github.io")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://1asi0729-2620-16692-bitmeisters.github.io"));
    }

    @Test
    void allowsTheLocalWebApplication() throws Exception {
        mockMvc.perform(get("/api/v1/ping").header("Origin", "http://localhost:4200").with(user("supervisor")))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void rejectsAnUnknownOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/ping")
                        .header("Origin", "https://example.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    void documentsTheApiWithTheProjectData() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("FleetSafe Platform API"))
                .andExpect(jsonPath("$.info.version").value(projectVersion))
                .andExpect(jsonPath("$.info.description").value("FleetSafe RESTful API for the preventive safety control of cargo fleets"));
    }
}
