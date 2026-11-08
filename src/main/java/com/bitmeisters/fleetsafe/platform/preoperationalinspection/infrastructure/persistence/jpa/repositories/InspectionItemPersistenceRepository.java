package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities.InspectionItemPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA persistence repository for inspection items.
 */
@Repository
public interface InspectionItemPersistenceRepository extends JpaRepository<InspectionItemPersistenceEntity, UUID> {

    /**
     * Finds an entity by its standardized item code.
     *
     * @param code the standardized item code
     * @return optional containing the matching entity, or empty
     */
    Optional<InspectionItemPersistenceEntity> findByCode(String code);

    /**
     * Checks if an entity exists with the specified item code.
     *
     * @param code the item code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Finds all active items ordered by their display sequence.
     *
     * @return ordered list of active items
     */
    List<InspectionItemPersistenceEntity> findAllByActiveTrueOrderByDisplayOrderAsc();

    /**
     * Finds all active items in a given category ordered by their display sequence.
     *
     * @param category the item category
     * @return ordered list of active items in category
     */
    List<InspectionItemPersistenceEntity> findAllByActiveTrueAndCategoryOrderByDisplayOrderAsc(ItemCategory category);

    /**
     * Queries inspection items applying optional system and active status filters.
     *
     * @param system optional system filter
     * @param active optional active status filter
     * @return ordered list of matching inspection items
     */
    @Query("""
            SELECT i FROM InspectionItemPersistenceEntity i
            WHERE (:system IS NULL OR i.system = :system)
              AND (:active IS NULL OR i.active = :active)
            ORDER BY i.displayOrder ASC
            """)
    List<InspectionItemPersistenceEntity> findAllByFilters(
            @Param("system") ItemSystem system,
            @Param("active") Boolean active);
}
