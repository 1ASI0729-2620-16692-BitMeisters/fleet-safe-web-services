package com.bitmeisters.fleetsafe.platform.fleet.domain.model.events;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;

public record DriverRegisteredEvent(CompanyId companyId, UserId userId) {
}
