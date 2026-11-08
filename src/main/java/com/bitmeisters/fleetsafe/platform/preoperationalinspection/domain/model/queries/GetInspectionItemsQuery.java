package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;

/**
 * Query to retrieve inspection items with optional filters by vehicle system and active status.
 */
public record GetInspectionItemsQuery(ItemSystem system, Boolean active) {
}
