package com.bitmeisters.fleetsafe.platform.fleet.application.internal.outboundservices.calendar;

import java.time.LocalDate;

/**
 * The calendar day of the company, which decides the day an assignment starts or ends and
 * whether a license is in force.
 */
public interface FleetCalendar {

    LocalDate today();
}
