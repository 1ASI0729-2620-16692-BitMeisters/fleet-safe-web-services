package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterCompanyCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyName;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.TaxId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * It does not extend the shared auditable entity because its identifier is not generated: a company
 * keeps the identifier its users already carry in their token.
 */
@Entity
@Table(name = "companies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tax_id"}, name = CompanyPersistenceEntity.TAX_ID_UNIQUE_CONSTRAINT)
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class CompanyPersistenceEntity {

    public static final String TAX_ID_UNIQUE_CONSTRAINT = "uk_company_tax_id";

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, length = CompanyName.MAX_LENGTH)
    private String name;

    @Column(name = "tax_id", nullable = false, length = TaxId.MAX_LENGTH)
    private String taxId;

    @Column(name = "address", length = RegisterCompanyCommand.ADDRESS_MAX_LENGTH)
    private String address;

    @Column(name = "phone", length = RegisterCompanyCommand.PHONE_MAX_LENGTH)
    private String phone;

    @Column(name = "email", length = RegisterCompanyCommand.EMAIL_MAX_LENGTH)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
