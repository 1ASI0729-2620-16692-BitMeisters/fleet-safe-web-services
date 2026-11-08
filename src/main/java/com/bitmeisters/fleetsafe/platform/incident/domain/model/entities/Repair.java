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
        this.incidentId = command.incidentId();
        this.workshop = command.workshop();
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

        repair.id = id;
        repair.status = status;
        return repair;
    }

    public void start() {
        if (this.status != RepairStatus.SCHEDULED) {
            throw new IllegalStateException("incident.repair.error.invalid.status");
        }

        this.startedAt = Timestamps.now();
        this.status = RepairStatus.IN_PROGRESS;
    }

    public void complete(Instant finishedAt) {
        if (finishedAt == null) {
            throw new IllegalArgumentException("Repair completion date cannot be null");
        }

        if (this.status == RepairStatus.COMPLETED) {
            throw new IllegalStateException("incident.repair.error.invalid.status");
        }

        if (this.startedAt != null && finishedAt.isBefore(this.startedAt)) {
            throw new IllegalArgumentException(
                    "Repair completion date cannot be before its start date"
            );
        }

        this.finishedAt = finishedAt;
        this.status = RepairStatus.COMPLETED;
    }
}