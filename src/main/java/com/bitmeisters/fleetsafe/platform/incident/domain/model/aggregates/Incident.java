package com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ReportIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentOrigin;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentSeverity;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.Timestamps;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Incident extends AbstractDomainAggregateRoot<Incident> {

    private UUID id;
    private UUID vehicleId;
    private UUID incidentTypeId;
    private UUID inspectionId;

    private IncidentOrigin origin;
    private String description;
    private IncidentSeverity severity;
    private IncidentStatus status;

    private UUID reportedBy;
    private Instant reportedAt;

    private String resolutionType;
    private Instant resolvedAt;

    private Instant createdAt;
    private Instant updatedAt;

    private Incident() {
    }

    public Incident(ReportIncidentCommand command) {
        this.vehicleId = command.vehicleId();
        this.incidentTypeId = command.incidentTypeId();
        this.inspectionId = command.inspectionId();
        this.origin = command.origin();
        this.description = command.description();
        this.severity = command.severity();
        this.status = IncidentStatus.REGISTERED;
        this.reportedBy = command.reportedBy();
        this.reportedAt = Timestamps.now();
        this.createdAt = this.reportedAt;
        this.updatedAt = this.reportedAt;
        this.resolutionType = null;
        this.resolvedAt = null;
    }

    public static Incident restore(
            UUID id,
            UUID vehicleId,
            UUID incidentTypeId,
            UUID inspectionId,
            IncidentOrigin origin,
            String description,
            IncidentSeverity severity,
            IncidentStatus status,
            UUID reportedBy,
            Instant reportedAt,
            String resolutionType,
            Instant resolvedAt,
            Instant createdAt,
            Instant updatedAt) {

        Incident incident = new Incident();

        incident.id = id;
        incident.vehicleId = vehicleId;
        incident.incidentTypeId = incidentTypeId;
        incident.inspectionId = inspectionId;
        incident.origin = origin;
        incident.description = description;
        incident.severity = severity;
        incident.status = status;
        incident.reportedBy = reportedBy;
        incident.reportedAt = reportedAt;
        incident.resolutionType = resolutionType;
        incident.resolvedAt = resolvedAt;
        incident.createdAt = createdAt;
        incident.updatedAt = updatedAt;

        return incident;
    }

    public void update(UpdateIncidentCommand command) {
        if (status == IncidentStatus.RESOLVED || status == IncidentStatus.CLOSED) {
            throw new IllegalStateException("incident.error.invalid-status");
        }

        this.vehicleId = command.vehicleId();
        this.incidentTypeId = command.incidentTypeId();
        this.inspectionId = command.inspectionId();
        this.origin = command.origin();
        this.description = command.description();
        this.severity = command.severity();
        this.reportedBy = command.reportedBy();
        this.updatedAt = Timestamps.now();
    }

    public void startReview() {
        if (status != IncidentStatus.REGISTERED) {
            throw new IllegalStateException("incident.error.invalid-status");
        }

        this.status = IncidentStatus.IN_REVIEW;
        this.updatedAt = Timestamps.now();
    }

    public void resolve(String resolutionType, Instant resolvedAt) {
        if (status != IncidentStatus.REGISTERED
                && status != IncidentStatus.IN_REVIEW) {
            throw new IllegalStateException("incident.error.invalid-status");
        }

        if (resolutionType == null || resolutionType.isBlank()) {
            throw new IllegalArgumentException("incident.error.invalid-resolution-type");
        }

        if (resolvedAt == null) {
            throw new IllegalArgumentException("incident.error.invalid-resolution-date");
        }

        this.status = IncidentStatus.RESOLVED;
        this.resolutionType = resolutionType;
        this.resolvedAt = resolvedAt;
        this.updatedAt = Timestamps.now();
    }

    public void close() {
        if (status != IncidentStatus.RESOLVED) {
            throw new IllegalStateException("incident.error.invalid-status");
        }

        this.status = IncidentStatus.CLOSED;
        this.updatedAt = Timestamps.now();
    }
}