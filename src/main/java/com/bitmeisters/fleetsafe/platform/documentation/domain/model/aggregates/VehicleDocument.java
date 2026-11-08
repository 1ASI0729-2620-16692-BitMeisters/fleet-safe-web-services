package com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.UpdateVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentStatus;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.ValidityPeriod;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class VehicleDocument extends AbstractDomainAggregateRoot<VehicleDocument> {

    public static final int ALERT_DAYS = 30;

    private UUID id;
    private VehicleId vehicleId;
    private DocumentTypeId documentTypeId;
    private DocumentNumber number;
    private ValidityPeriod validityPeriod;
    private DocumentStatus status;
    private FileUrl fileUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public VehicleDocument(RegisterVehicleDocumentCommand command, LocalDate today) {
        this.vehicleId = command.vehicleId();
        this.documentTypeId = command.documentTypeId();
        this.number = command.number();
        this.validityPeriod = command.validityPeriod();
        this.fileUrl = command.fileUrl();
        recalculateStatus(today);
    }

    public static VehicleDocument restore(UUID id, VehicleId vehicleId, DocumentTypeId documentTypeId, DocumentNumber number,
                                          ValidityPeriod validityPeriod, DocumentStatus status, FileUrl fileUrl,
                                          Instant createdAt, Instant updatedAt) {
        var document = new VehicleDocument(
                new RegisterVehicleDocumentCommand(vehicleId, documentTypeId, number, validityPeriod, fileUrl),
                validityPeriod.issueDate());
        document.id = id;
        document.status = status;
        document.createdAt = createdAt;
        document.updatedAt = updatedAt;
        return document;
    }

    public void update(UpdateVehicleDocumentCommand command, LocalDate today) {
        this.documentTypeId = command.documentTypeId();
        this.number = command.number();
        this.validityPeriod = command.validityPeriod();
        this.fileUrl = command.fileUrl();
        recalculateStatus(today);
    }

    public long daysUntilExpiration(LocalDate today) {
        return ChronoUnit.DAYS.between(today, validityPeriod.expirationDate());
    }

    public DocumentStatus recalculateStatus(LocalDate today) {
        var days = daysUntilExpiration(today);
        if (days < 0) this.status = DocumentStatus.EXPIRED;
        else if (days <= ALERT_DAYS) this.status = DocumentStatus.EXPIRING;
        else this.status = DocumentStatus.VALID;
        return this.status;
    }

    public boolean isExpired(LocalDate today) {
        return daysUntilExpiration(today) < 0;
    }

    public boolean belongsTo(VehicleId vehicleId) {
        return this.vehicleId.equals(vehicleId);
    }
}
