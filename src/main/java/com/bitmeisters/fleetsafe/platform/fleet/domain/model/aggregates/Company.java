package com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterCompanyCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyName;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.TaxId;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * A transport company that owns one or more fleets. Its identifier is the company identifier of the
 * users who belong to it, which is how every other context already refers to the company.
 */
@Getter
public class Company extends AbstractDomainAggregateRoot<Company> {

    private UUID id;
    private CompanyName name;
    private TaxId taxId;
    private String address;
    private String phone;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    private Company() {
    }

    public Company(RegisterCompanyCommand command) {
        this.id = command.companyId().value();
        this.name = command.name();
        this.taxId = command.taxId();
        this.address = command.address();
        this.phone = command.phone();
        this.email = command.email();
    }

    public static Company restore(UUID id, CompanyName name, TaxId taxId, String address, String phone, String email,
                                  Instant createdAt, Instant updatedAt) {
        var company = new Company();
        company.id = id;
        company.name = name;
        company.taxId = taxId;
        company.address = address;
        company.phone = phone;
        company.email = email;
        company.createdAt = createdAt;
        company.updatedAt = updatedAt;
        return company;
    }
}
