package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LicenseNumber;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "drivers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"}, name = DriverPersistenceEntity.USER_UNIQUE_CONSTRAINT)
})
@Getter
@Setter
@NoArgsConstructor
public class DriverPersistenceEntity extends AuditableAbstractPersistenceEntity {

    public static final String USER_UNIQUE_CONSTRAINT = "uk_driver_user";

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "license_number", nullable = false, length = LicenseNumber.MAX_LENGTH)
    private String licenseNumber;

    @Column(name = "license_expiration_date", nullable = false)
    private LocalDate licenseExpirationDate;
}
