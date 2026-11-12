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
        if (command == null) {
            throw new IllegalArgumentException("incident.error.command.required");
        }

        this.vehicleId = command.vehicleId();
        this.incidentTypeId = command.incidentTypeId();
        this.inspectionId = command.inspectionId();

        this.origin = command.origin();
        this.description = normalizeDescription(command.description());
        this.severity = command.severity();

        this.status = IncidentStatus.REGISTERED;

        this.reportedBy = command.reportedBy();
        this.reportedAt = Timestamps.now();

        this.resolutionType = null;
        this.resolvedAt = null;

        this.createdAt = this.reportedAt;
        this.updatedAt = this.reportedAt;
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
        if (command == null) {
            throw new IllegalArgumentException("incident.error.command.required");
        }

        ensureEditable();

        this.vehicleId = command.vehicleId();
        this.incidentTypeId = command.incidentTypeId();
        this.inspectionId = command.inspectionId();

        this.origin = command.origin();
        this.description = normalizeDescription(command.description());
        this.severity = command.severity();

        // reportedBy and reportedAt are historical data.
        // They must not change when an incident is updated.

        this.updatedAt = Timestamps.now();
    }

    public void assertCanRegisterCorrectiveAction() {
        if (status != IncidentStatus.IN_REVIEW) {
            throw new IllegalStateException(
                    "incident.error.corrective-action.invalid-status"
            );
        }
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
            throw new IllegalArgumentException(
                    "incident.error.invalid-resolution-type");
        }

        if (resolvedAt == null) {
            throw new IllegalArgumentException(
                    "incident.error.invalid-resolution-date");
        }

        if (reportedAt != null && resolvedAt.isBefore(reportedAt)) {
            throw new IllegalArgumentException(
                    "incident.error.resolution-date-before-report");
        }

        this.status = IncidentStatus.RESOLVED;
        this.resolutionType = resolutionType.trim();
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

    private void ensureEditable() {
        if (status == IncidentStatus.RESOLVED
                || status == IncidentStatus.CLOSED) {
            throw new IllegalStateException("incident.error.invalid-status");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "incident.error.description.required");
        }

        return description.trim();
    }
}