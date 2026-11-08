package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleRepository;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers.VehiclePersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories.VehiclePersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehiclePersistenceRepository vehiclePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public VehicleRepositoryImpl(VehiclePersistenceRepository vehiclePersistenceRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.vehiclePersistenceRepository = vehiclePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Vehicle> findByIdAndCompanyId(VehicleId id, CompanyId companyId) {
        return vehiclePersistenceRepository.findByIdAndCompanyId(id.value(), companyId.value())
                .map(VehiclePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Vehicle> findAllByCompanyId(CompanyId companyId) {
        return vehiclePersistenceRepository.findAllByCompanyId(companyId.value()).stream()
                .map(VehiclePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Vehicle> findAllByCompanyIdAndStatus(CompanyId companyId, VehicleStatus status) {
        return vehiclePersistenceRepository.findAllByCompanyIdAndStatus(companyId.value(), status).stream()
                .map(VehiclePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByPlate(Plate plate) {
        return vehiclePersistenceRepository.existsByPlate(plate.value());
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        var events = List.copyOf(vehicle.domainEvents());
        // Flushed so that the audit timestamps of an update are already set in what is returned.
        var saved = vehiclePersistenceRepository.saveAndFlush(VehiclePersistenceAssembler.toPersistenceFromDomain(vehicle));
        events.forEach(eventPublisher::publishEvent);
        vehicle.clearDomainEvents();
        return VehiclePersistenceAssembler.toDomainFromPersistence(saved);
    }
}
