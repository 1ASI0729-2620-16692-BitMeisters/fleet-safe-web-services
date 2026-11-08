package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.calendar;

import com.bitmeisters.fleetsafe.platform.documentation.application.internal.outboundservices.calendar.DocumentationCalendar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class ZonedDocumentationCalendar implements DocumentationCalendar {

    private final ZoneId zone;

    public ZonedDocumentationCalendar(@Value("${vehicle-documentation.time-zone:America/Lima}") String zone) {
        this.zone = ZoneId.of(zone);
    }

    @Override
    public LocalDate today() {
        return LocalDate.now(zone);
    }
}
