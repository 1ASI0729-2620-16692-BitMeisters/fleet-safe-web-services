package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;

import java.util.List;
import java.util.Optional;

/**
 * InspectionItemRepository port interface.
 *
 * <p>
 * Defines domain repository operations for persisting and retrieving checklist catalog items.
 * </p>
 */
public interface InspectionItemRepository {

    /**
     * Finds an inspection item by its unique identifier.
     *
     * @param id the item identifier
     * @return optional containing the found item, or empty
     */
    Optional<InspectionItem> findById(InspectionItemId id);

    /**
     * Finds an inspection item by its standardized code.
     *
     * @param code the item code
     * @return optional containing the found item, or empty
     */
    Optional<InspectionItem> findByCode(ItemCode code);

    /**
     * Retrieves all inspection items.
     *
     * @return list of all items
     */
    List<InspectionItem> findAll();

    /**
     * Retrieves all active inspection items ordered by their display sequence.
     *
     * @return ordered list of active items
     */
    List<InspectionItem> findAllByActiveTrueOrderByDisplayOrderAsc();

    /**
     * Retrieves all active inspection items belonging to a specific category.
     *
     * @param category the category filter
     * @return ordered list of matching active items
     */
    List<InspectionItem> findAllByActiveTrueAndCategoryOrderByDisplayOrderAsc(ItemCategory category);

    /**
     * Saves an inspection item aggregate.
     *
     * @param inspectionItem the item to save
     * @return the saved inspection item
     */
    InspectionItem save(InspectionItem inspectionItem);

    /**
     * Checks if an inspection item exists by its identifier.
     *
     * @param id the item identifier
     * @return true if exists, false otherwise
     */
    boolean existsById(InspectionItemId id);

    /**
     * Checks if an inspection item exists with the given code.
     *
     * @param code the item code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(ItemCode code);
}