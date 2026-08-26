package com.rathinam.toastmasters;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.sql.DataSource;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
class RathinamApplicationTests {

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @Test
    void contextLoads() {
    }
}
