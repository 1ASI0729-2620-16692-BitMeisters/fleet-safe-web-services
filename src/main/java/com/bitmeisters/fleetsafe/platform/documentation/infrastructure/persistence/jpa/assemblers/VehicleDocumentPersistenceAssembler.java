package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.ValidityPeriod;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.entities.VehicleDocumentPersistenceEntity;

public final class VehicleDocumentPersistenceAssembler {

    private VehicleDocumentPersistenceAssembler() {
    }

    public static VehicleDocument toDomainFromPersistence(VehicleDocumentPersistenceEntity entity) {
        if (entity == null) return null;
        return VehicleDocument.restore(
                entity.getId(),
                new VehicleId(entity.getVehicleId()),
                new DocumentTypeId(entity.getDocumentTypeId()),
                new DocumentNumber(entity.getNumber()),
                new ValidityPeriod(entity.getIssueDate(), entity.getExpirationDate()),
                entity.getStatus(),
                entity.getFileUrl() == null ? null : new FileUrl(entity.getFileUrl()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static VehicleDocumentPersistenceEntity toPersistenceFromDomain(VehicleDocument vehicleDocument) {
        if (vehicleDocument == null) return null;
        var entity = new VehicleDocumentPersistenceEntity();
        if (vehicleDocument.getId() != null) {
            entity.setId(vehicleDocument.getId());
            entity.setCreatedAt(vehicleDocument.getCreatedAt());
        }
        entity.setVehicleId(vehicleDocument.getVehicleId().value());
        entity.setDocumentTypeId(vehicleDocument.getDocumentTypeId().value());
        entity.setNumber(vehicleDocument.getNumber().value());
        entity.setIssueDate(vehicleDocument.getValidityPeriod().issueDate());
        entity.setExpirationDate(vehicleDocument.getValidityPeriod().expirationDate());
        entity.setStatus(vehicleDocument.getStatus());
        entity.setFileUrl(vehicleDocument.getFileUrl() == null ? null : vehicleDocument.getFileUrl().value());
        return entity;
    }
}
