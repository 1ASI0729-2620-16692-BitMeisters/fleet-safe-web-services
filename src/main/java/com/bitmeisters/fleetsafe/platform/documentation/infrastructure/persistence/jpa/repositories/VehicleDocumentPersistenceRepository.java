package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.entities.VehicleDocumentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleDocumentPersistenceRepository extends JpaRepository<VehicleDocumentPersistenceEntity, UUID> {

    List<VehicleDocumentPersistenceEntity> findAllByVehicleId(UUID vehicleId);

    List<VehicleDocumentPersistenceEntity> findAllByExpirationDateBetween(LocalDate from, LocalDate to);
}
