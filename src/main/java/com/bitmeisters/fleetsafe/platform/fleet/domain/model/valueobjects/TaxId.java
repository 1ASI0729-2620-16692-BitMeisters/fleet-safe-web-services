package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

/**
 * Tax identifier of a company (RUC in Peru). It is unique among companies.
 */
public record TaxId(String value) {

    public static final int MAX_LENGTH = 30;

    public TaxId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("fleet.company.error.tax-id.required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("fleet.company.error.tax-id.size");
        }
    }
}
