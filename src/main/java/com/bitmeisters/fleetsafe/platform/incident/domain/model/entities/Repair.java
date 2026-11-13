package com.bitmeisters.fleetsafe.platform.incident.domain.model.entities;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ScheduleRepairCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.RepairStatus;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.Timestamps;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Repair {

    private UUID id;
    private UUID incidentId;
    private String workshop;
    private BigDecimal cost;
    private Instant startedAt;
    private Instant finishedAt;
    private RepairStatus status;

    public Repair(ScheduleRepairCommand command) {
        if (command == null) {
            throw new IllegalArgumentException(
                    "incident.repair.error.command.required");
        }

        this.incidentId = command.incidentId();
        this.workshop = command.workshop().trim();
        this.cost = command.cost();
        this.startedAt = command.startedAt();
        this.finishedAt = command.finishedAt();
        this.status = RepairStatus.SCHEDULED;
    }

    public static Repair restore(
            UUID id,
            UUID incidentId,
            String workshop,
            BigDecimal cost,
            Instant startedAt,
            Instant finishedAt,
            RepairStatus status) {

        var repair = new Repair(
                new ScheduleRepairCommand(
                        incidentId,
                        workshop,
                        cost,
                        startedAt,
                        finishedAt));

        if (status == null) {
            throw new IllegalArgumentException(
                    "incident.repair.error.status.required");
        }

        repair.id = id;
        repair.status = status;
        return repair;
    }

    public void start() {
        if (this.status != RepairStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "incident.repair.error.invalid-status");
        }

        this.startedAt = Timestamps.now();
        this.status = RepairStatus.IN_PROGRESS;
    }

    public void complete(Instant finishedAt) {
        if (this.status != RepairStatus.SCHEDULED
                && this.status != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "incident.repair.error.invalid-status");
        }

        if (finishedAt == null) {
            throw new IllegalArgumentException(
                    "incident.repair.error.finished-at.required");
        }

        if (this.startedAt != null
                && finishedAt.isBefore(this.startedAt)) {
            throw new IllegalArgumentException(
                    "incident.repair.error.finished-at.invalid");
        }

        this.finishedAt = finishedAt;
        this.status = RepairStatus.COMPLETED;
    }
}