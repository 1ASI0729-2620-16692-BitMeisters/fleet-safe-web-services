package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.VehicleDocumentRepository;
import com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.assemblers.VehicleDocumentPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.repositories.VehicleDocumentPersistenceRepository;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleDocumentRepositoryImpl implements VehicleDocumentRepository {

    private final VehicleDocumentPersistenceRepository vehicleDocumentPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public VehicleDocumentRepositoryImpl(VehicleDocumentPersistenceRepository vehicleDocumentPersistenceRepository,
                                         DomainEventPublisher domainEventPublisher) {
        this.vehicleDocumentPersistenceRepository = vehicleDocumentPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<VehicleDocument> findById(UUID id) {
        return vehicleDocumentPersistenceRepository.findById(id).map(VehicleDocumentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<VehicleDocument> findAll() {
        return vehicleDocumentPersistenceRepository.findAll().stream()
                .map(VehicleDocumentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<VehicleDocument> findAllByVehicleId(VehicleId vehicleId) {
        return vehicleDocumentPersistenceRepository.findAllByVehicleId(vehicleId.value()).stream()
                .map(VehicleDocumentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<VehicleDocument> findAllByExpirationDateBetween(LocalDate from, LocalDate to) {
        return vehicleDocumentPersistenceRepository.findAllByExpirationDateBetween(from, to).stream()
                .map(VehicleDocumentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public VehicleDocument save(VehicleDocument vehicleDocument) {
        var saved = vehicleDocumentPersistenceRepository.save(VehicleDocumentPersistenceAssembler.toPersistenceFromDomain(vehicleDocument));
        domainEventPublisher.publishAndClearEvents(vehicleDocument);
        return VehicleDocumentPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
