package com.rathinam.toastmasters.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @Test
    void publicEndpointsShouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/members"))
            .andExpect(status().isForbidden());
    }

    @Test
    void h2ConsoleShouldNoLongerBePermitted() throws Exception {
        mockMvc.perform(get("/h2-console"))
            .andExpect(status().isForbidden());
    }
}
