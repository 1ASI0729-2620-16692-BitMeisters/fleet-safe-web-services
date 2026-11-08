package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.Evidence;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.InspectionResult;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.Observation;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.EvidenceResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.InspectionResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.InspectionResultResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.ObservationResource;

import java.util.function.Function;

public final class InspectionResourceFromEntityAssembler {

    private InspectionResourceFromEntityAssembler() {
    }

    public static InspectionResource toResourceFromEntity(Inspection inspection, Function<FileUrl, String> readUrl) {
        return new InspectionResource(
                inspection.getId().value(),
                inspection.getVehicleId().value(),
                inspection.getDriverId().value(),
                inspection.getOdometer().value(),
                inspection.getStatus().name(),
                inspection.getStartedAt(),
                inspection.getCompletedAt(),
                inspection.getResults().stream().map(result -> toResourceFromEntity(result, readUrl)).toList());
    }

    public static InspectionResultResource toResourceFromEntity(InspectionResult result, Function<FileUrl, String> readUrl) {
        return new InspectionResultResource(
                result.getId().value(),
                result.getInspectionItemId().value(),
                result.getItemName().value(),
                result.getItemCategory().name(),
                result.getResult().name(),
                result.getObservations().stream().map(observation -> toResourceFromEntity(observation, readUrl)).toList());
    }

    public static ObservationResource toResourceFromEntity(Observation observation, Function<FileUrl, String> readUrl) {
        return new ObservationResource(
                observation.getId().value(),
                observation.getDescription().value(),
                observation.getCreatedBy(),
                observation.getCreatedAt(),
                observation.getEvidences().stream().map(evidence -> toResourceFromEntity(evidence, readUrl)).toList());
    }

    public static EvidenceResource toResourceFromEntity(Evidence evidence, Function<FileUrl, String> readUrl) {
        return new EvidenceResource(
                evidence.getId().value(),
                readUrl.apply(evidence.getFileUrl()),
                evidence.getMediaType().getValue(),
                evidence.getUploadedAt());
    }
}
