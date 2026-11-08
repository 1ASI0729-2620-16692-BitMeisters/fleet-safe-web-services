package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LoadCapacity;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleBrand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleModel;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleType;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "vehicles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"plate"}, name = VehiclePersistenceEntity.PLATE_UNIQUE_CONSTRAINT)
})
@Getter
@Setter
@NoArgsConstructor
public class VehiclePersistenceEntity extends AuditableAbstractPersistenceEntity {

    public static final String PLATE_UNIQUE_CONSTRAINT = "uk_vehicle_plate";

    @Column(name = "fleet_id", nullable = false)
    private UUID fleetId;

    @Column(name = "plate", nullable = false, length = Plate.MAX_LENGTH)
    private String plate;

    @Column(name = "brand", nullable = false, length = VehicleBrand.MAX_LENGTH)
    private String brand;

    @Column(name = "model", nullable = false, length = VehicleModel.MAX_LENGTH)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(name = "capacity", nullable = false, precision = LoadCapacity.PRECISION, scale = LoadCapacity.SCALE)
    private BigDecimal capacity;

    @Column(name = "current_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleStatus currentStatus;
}
