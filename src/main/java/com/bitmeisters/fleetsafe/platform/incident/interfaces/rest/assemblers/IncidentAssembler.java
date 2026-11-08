package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.IncidentResource;

import java.util.List;

public class IncidentAssembler {

    private final CorrectiveActionAssembler correctiveActionAssembler;
    private final RepairAssembler repairAssembler;
    private final IncidentFollowUpAssembler incidentFollowUpAssembler;

    public IncidentAssembler() {
        this.correctiveActionAssembler = new CorrectiveActionAssembler();
        this.repairAssembler = new RepairAssembler();
        this.incidentFollowUpAssembler = new IncidentFollowUpAssembler();
    }

    public IncidentResource toResourceFromEntity(
            Incident entity,
            List<CorrectiveAction> correctiveActions,
            List<Repair> repairs,
            List<IncidentFollowUp> followUps
    ) {
        return new IncidentResource(
                entity.getId(),
                entity.getVehicleId(),
                entity.getIncidentTypeId(),
                entity.getInspectionId(),
                entity.getOrigin(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getReportedBy(),
                entity.getReportedAt(),
                entity.getResolutionType(),
                entity.getResolvedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                correctiveActions.stream()
                        .map(correctiveActionAssembler::toResourceFromEntity)
                        .toList(),
                repairs.stream()
                        .map(repairAssembler::toResourceFromEntity)
                        .toList(),
                followUps.stream()
                        .map(incidentFollowUpAssembler::toResourceFromEntity)
                        .toList()
        );
    }
}