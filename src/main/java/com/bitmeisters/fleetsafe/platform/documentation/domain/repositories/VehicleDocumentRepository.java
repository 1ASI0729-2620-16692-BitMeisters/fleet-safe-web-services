package com.bitmeisters.fleetsafe.platform.documentation.domain.repositories;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleDocumentRepository {

    Optional<VehicleDocument> findById(UUID id);

    List<VehicleDocument> findAll();

    List<VehicleDocument> findAllByVehicleId(VehicleId vehicleId);

    List<VehicleDocument> findAllByExpirationDateBetween(LocalDate from, LocalDate to);

    VehicleDocument save(VehicleDocument vehicleDocument);
}
