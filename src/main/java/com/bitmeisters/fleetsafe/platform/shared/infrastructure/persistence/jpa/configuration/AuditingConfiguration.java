package com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.configuration;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.Timestamps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AuditingConfiguration {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(Timestamps.now());
    }
}
