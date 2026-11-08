package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.Evidence;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.InspectionResult;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.Observation;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.EvidenceId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationDescription;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.Odometer;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities.EvidencePersistenceEntity;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities.InspectionPersistenceEntity;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities.InspectionResultPersistenceEntity;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities.ObservationPersistenceEntity;

/**
 * Assembler to map between the Inspection domain aggregate hierarchy and JPA persistence entities.
 */
public final class InspectionPersistenceAssembler {

    private InspectionPersistenceAssembler() {
    }

    /**
     * Converts a JPA persistence entity hierarchy into a rich Inspection domain aggregate.
     *
     * @param entity the root persistence entity
     * @return reconstituted Inspection domain aggregate
     */
    public static Inspection toDomainFromPersistence(InspectionPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        var results = entity.getResults().stream()
                .map(InspectionPersistenceAssembler::toDomainResult)
                .toList();

        return Inspection.restore(
                new InspectionId(entity.getId()),
                new VehicleId(entity.getVehicleId()),
                new DriverId(entity.getDriverId()),
                entity.getStatus(),
                new Odometer(entity.getOdometer()),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                results,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static InspectionResult toDomainResult(InspectionResultPersistenceEntity resultEntity) {
        var observations = resultEntity.getObservations().stream()
                .map(InspectionPersistenceAssembler::toDomainObservation)
                .toList();

        return InspectionResult.restore(
                new InspectionResultId(resultEntity.getId()),
                new InspectionItemId(resultEntity.getInspectionItemId()),
                new ItemName(resultEntity.getItemName()),
                resultEntity.getItemCategory(),
                resultEntity.getResultValue(),
                resultEntity.getCreatedAt(),
                observations);
    }

    private static Observation toDomainObservation(ObservationPersistenceEntity obsEntity) {
        var evidences = obsEntity.getEvidences().stream()
                .map(InspectionPersistenceAssembler::toDomainEvidence)
                .toList();

        return Observation.restore(
                new ObservationId(obsEntity.getId()),
                new ObservationDescription(obsEntity.getDescription()),
                obsEntity.getCreatedBy(),
                obsEntity.getCreatedAt(),
                evidences);
    }

    private static Evidence toDomainEvidence(EvidencePersistenceEntity evEntity) {
        return Evidence.restore(
                new EvidenceId(evEntity.getId()),
                new FileUrl(evEntity.getFileUrl()),
                evEntity.getMediaType(),
                evEntity.getUploadedAt());
    }

    /**
     * Converts a domain Inspection aggregate hierarchy into a JPA persistence entity hierarchy.
     *
     * @param inspection the domain aggregate
     * @return JPA persistence entity ready for storage
     */
    public static InspectionPersistenceEntity toPersistenceFromDomain(Inspection inspection) {
        if (inspection == null) {
            return null;
        }

        var entity = new InspectionPersistenceEntity();
        if (inspection.getId() != null) {
            entity.setId(inspection.getId().value());
            entity.setCreatedAt(inspection.getCreatedAt());
        }
        entity.setVehicleId(inspection.getVehicleId().value());
        entity.setDriverId(inspection.getDriverId().value());
        entity.setStatus(inspection.getStatus());
        entity.setOdometer(inspection.getOdometer().value());
        entity.setStartedAt(inspection.getStartedAt());
        entity.setCompletedAt(inspection.getCompletedAt());

        inspection.getResults().forEach(result -> {
            var resultEntity = new InspectionResultPersistenceEntity();
            if (result.getId() != null) {
                resultEntity.setId(result.getId().value());
                resultEntity.setCreatedAt(result.getCreatedAt());
            }
            resultEntity.setInspection(entity);
            resultEntity.setInspectionItemId(result.getInspectionItemId().value());
            resultEntity.setItemName(result.getItemName().value());
            resultEntity.setItemCategory(result.getItemCategory());
            resultEntity.setResultValue(result.getResult());

            result.getObservations().forEach(obs -> {
                var obsEntity = new ObservationPersistenceEntity();
                if (obs.getId() != null) {
                    obsEntity.setId(obs.getId().value());
                    obsEntity.setCreatedAt(obs.getCreatedAt());
                }
                obsEntity.setInspectionResult(resultEntity);
                obsEntity.setDescription(obs.getDescription().value());
                obsEntity.setCreatedBy(obs.getCreatedBy());

                obs.getEvidences().forEach(ev -> {
                    var evEntity = new EvidencePersistenceEntity();
                    if (ev.getId() != null) {
                        evEntity.setId(ev.getId().value());
                        evEntity.setCreatedAt(ev.getUploadedAt());
                    }
                    evEntity.setObservation(obsEntity);
                    evEntity.setFileUrl(ev.getFileUrl().value());
                    evEntity.setMediaType(ev.getMediaType());
                    evEntity.setUploadedAt(ev.getUploadedAt());
                    obsEntity.getEvidences().add(evEntity);
                });

                resultEntity.getObservations().add(obsEntity);
            });

            entity.getResults().add(resultEntity);
        });

        return entity;
    }
}
