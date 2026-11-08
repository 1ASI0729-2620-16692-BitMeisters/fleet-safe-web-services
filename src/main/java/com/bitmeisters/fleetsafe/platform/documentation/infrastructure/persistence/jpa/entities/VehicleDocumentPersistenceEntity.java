package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentStatus;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vehicle_documents")
@Getter
@Setter
@NoArgsConstructor
public class VehicleDocumentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "document_type_id", nullable = false)
    private UUID documentTypeId;

    @Column(name = "number", nullable = false, length = DocumentNumber.MAX_LENGTH)
    private String number;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @Column(name = "file_url", length = FileUrl.MAX_LENGTH)
    private String fileUrl;
}
