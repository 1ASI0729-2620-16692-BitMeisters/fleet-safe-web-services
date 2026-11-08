package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories.InspectionItemRepository;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.assemblers.InspectionItemPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.repositories.InspectionItemPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of the domain InspectionItemRepository port backed by Spring Data JPA.
 */
@Repository
public class InspectionItemRepositoryImpl implements InspectionItemRepository {

    private final InspectionItemPersistenceRepository inspectionItemPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructs the repository adapter with required persistence repository and event publisher.
     *
     * @param inspectionItemPersistenceRepository the Spring Data JPA repository
     * @param eventPublisher                      the Spring event publisher
     */
    public InspectionItemRepositoryImpl(InspectionItemPersistenceRepository inspectionItemPersistenceRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.inspectionItemPersistenceRepository = inspectionItemPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<InspectionItem> findById(InspectionItemId id) {
        return inspectionItemPersistenceRepository.findById(id.value())
                .map(InspectionItemPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<InspectionItem> findByCode(ItemCode code) {
        return inspectionItemPersistenceRepository.findByCode(code.value())
                .map(InspectionItemPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<InspectionItem> findAll() {
        return inspectionItemPersistenceRepository.findAll().stream()
                .map(InspectionItemPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<InspectionItem> findAllByActiveTrueOrderByDisplayOrderAsc() {
        return inspectionItemPersistenceRepository.findAllByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(InspectionItemPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<InspectionItem> findAllByActiveTrueAndCategoryOrderByDisplayOrderAsc(ItemCategory category) {
        return inspectionItemPersistenceRepository.findAllByActiveTrueAndCategoryOrderByDisplayOrderAsc(category).stream()
                .map(InspectionItemPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<InspectionItem> findAllByFilters(ItemSystem system, Boolean active) {
        return inspectionItemPersistenceRepository.findAllByFilters(system, active).stream()
                .map(InspectionItemPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public InspectionItem save(InspectionItem inspectionItem) {
        var events = List.copyOf(inspectionItem.domainEvents());
        var saved = inspectionItemPersistenceRepository.save(
                InspectionItemPersistenceAssembler.toPersistenceFromDomain(inspectionItem));
        events.forEach(eventPublisher::publishEvent);
        inspectionItem.clearDomainEvents();
        return InspectionItemPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsById(InspectionItemId id) {
        return inspectionItemPersistenceRepository.existsById(id.value());
    }

    @Override
    public boolean existsByCode(ItemCode code) {
        return inspectionItemPersistenceRepository.existsByCode(code.value());
    }
}
