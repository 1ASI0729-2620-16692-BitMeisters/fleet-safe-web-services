package com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.seeding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class DemoDataSeeder {

    private static final String DEMO_DATA = "db/demo-data.sql";

    private final DataSource dataSource;
    private final boolean enabled;
    private final String companyId;

    public DemoDataSeeder(DataSource dataSource,
                          @Value("${demo-data.enabled:false}") boolean enabled,
                          @Value("${iam.administrator.company-id:}") String companyId) {
        this.dataSource = dataSource;
        this.enabled = enabled;
        this.companyId = companyId;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() throws IOException {
        if (!enabled) return;
        if (!StringUtils.hasText(companyId)) {
            log.warn("Demo data skipped: ADMIN_COMPANY_ID is not set");
            return;
        }
        var script = new String(new ClassPathResource(DEMO_DATA).getContentAsByteArray(), StandardCharsets.UTF_8)
                .replace(":companyId", UUID.fromString(companyId).toString());
        new ResourceDatabasePopulator(new ByteArrayResource(script.getBytes(StandardCharsets.UTF_8))).execute(dataSource);
        log.info("Demo data loaded for company {}", companyId);
    }
}
