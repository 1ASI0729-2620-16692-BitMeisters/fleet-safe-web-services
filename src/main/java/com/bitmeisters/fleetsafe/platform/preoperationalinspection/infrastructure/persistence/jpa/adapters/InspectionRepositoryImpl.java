package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionStatus;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories.InspectionRepository;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.assemblers.InspectionPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.repositories.InspectionPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of the domain InspectionRepository port backed by Spring Data JPA.
 *
 * <p>
 * Handles persistence of the Inspection aggregate hierarchy and publishes registered domain events
 * upon saving.
 * </p>
 */
@Repository
public class InspectionRepositoryImpl implements InspectionRepository {

    private final InspectionPersistenceRepository inspectionPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructs the repository adapter with required persistence repository and event publisher.
     *
     * @param inspectionPersistenceRepository the Spring Data JPA repository
     * @param eventPublisher                  the Spring event publisher
     */
    public InspectionRepositoryImpl(InspectionPersistenceRepository inspectionPersistenceRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.inspectionPersistenceRepository = inspectionPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Inspection> findById(InspectionId id) {
        return inspectionPersistenceRepository.findById(id.value())
                .map(InspectionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Inspection> findAll() {
        return inspectionPersistenceRepository.findAll().stream()
                .map(InspectionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByVehicleIdAndStatus(VehicleId vehicleId, InspectionStatus status) {
        return inspectionPersistenceRepository.existsByVehicleIdAndStatus(vehicleId.value(), status);
    }

    @Override
    public List<Inspection> findAllByVehicleIdOrderByStartedAtDesc(VehicleId vehicleId) {
        return inspectionPersistenceRepository.findAllByVehicleIdOrderByStartedAtDesc(vehicleId.value()).stream()
                .map(InspectionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Inspection> findAllByDriverIdOrderByStartedAtDesc(DriverId driverId) {
        return inspectionPersistenceRepository.findAllByDriverIdOrderByStartedAtDesc(driverId.value()).stream()
                .map(InspectionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Inspection save(Inspection inspection) {
        var events = List.copyOf(inspection.domainEvents());
        var saved = inspectionPersistenceRepository.save(
                InspectionPersistenceAssembler.toPersistenceFromDomain(inspection));
        events.forEach(eventPublisher::publishEvent);
        inspection.clearDomainEvents();
        return InspectionPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
