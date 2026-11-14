package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleAssignmentId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleAssignmentRepository;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers.VehicleAssignmentPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories.VehicleAssignmentPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehicleAssignmentRepositoryImpl implements VehicleAssignmentRepository {

    private final VehicleAssignmentPersistenceRepository vehicleAssignmentPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public VehicleAssignmentRepositoryImpl(VehicleAssignmentPersistenceRepository vehicleAssignmentPersistenceRepository,
                                           ApplicationEventPublisher eventPublisher) {
        this.vehicleAssignmentPersistenceRepository = vehicleAssignmentPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<VehicleAssignment> findByIdAndCompanyId(VehicleAssignmentId id, CompanyId companyId) {
        return vehicleAssignmentPersistenceRepository.findByIdAndCompanyId(id.value(), companyId.value())
                .map(VehicleAssignmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<VehicleAssignment> findAllByCompanyId(CompanyId companyId) {
        return vehicleAssignmentPersistenceRepository.findAllByCompanyId(companyId.value()).stream()
                .map(VehicleAssignmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<VehicleAssignment> findAllActiveByCompanyId(CompanyId companyId) {
        return vehicleAssignmentPersistenceRepository.findAllActiveByCompanyId(companyId.value()).stream()
                .map(VehicleAssignmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<VehicleAssignment> findActiveByDriverId(DriverId driverId) {
        return vehicleAssignmentPersistenceRepository.findByDriverIdAndActiveTrue(driverId.value())
                .map(VehicleAssignmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsActiveByVehicleId(VehicleId vehicleId) {
        return vehicleAssignmentPersistenceRepository.existsByVehicleIdAndActiveTrue(vehicleId.value());
    }

    @Override
    public boolean existsActiveByDriverId(DriverId driverId) {
        return vehicleAssignmentPersistenceRepository.existsByDriverIdAndActiveTrue(driverId.value());
    }

    @Override
    public VehicleAssignment save(VehicleAssignment assignment) {
        var events = List.copyOf(assignment.domainEvents());
        var saved = vehicleAssignmentPersistenceRepository.saveAndFlush(
                VehicleAssignmentPersistenceAssembler.toPersistenceFromDomain(assignment));
        events.forEach(eventPublisher::publishEvent);
        assignment.clearDomainEvents();
        return VehicleAssignmentPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
