package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.calendar;

import com.bitmeisters.fleetsafe.platform.fleet.application.internal.outboundservices.calendar.FleetCalendar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class ZonedFleetCalendar implements FleetCalendar {

    private final ZoneId zone;

    public ZonedFleetCalendar(@Value("${fleet.time-zone:America/Lima}") String zone) {
        this.zone = ZoneId.of(zone);
    }

    @Override
    public LocalDate today() {
        return LocalDate.now(zone);
    }
}
