package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * InspectionItem aggregate root.
 *
 * <p>
 * Represents an individual component, safety equipment, or documentation requirement
 * within the checklist catalog of the pre-operational inspection domain.
 * </p>
 */
@Getter
public class InspectionItem extends AbstractDomainAggregateRoot<InspectionItem> {

    /**
     * The unique identifier for the inspection item.
     */
    private InspectionItemId id;

    /**
     * The standardized code of the item (e.g., BRK-01, SAF-02).
     */
    private ItemCode code;

    /**
     * The descriptive name of the item.
     */
    private ItemName name;

    /**
     * Detailed instructions or verification criteria for this item.
     */
    private String description;

    /**
     * The category classification of the item.
     */
    private ItemCategory category;

    /**
     * The vehicle system this item belongs to.
     */
    private ItemSystem system;

    /**
     * Indicates whether this item directly impacts the operational safety of the vehicle.
     */
    private boolean safetyComponent;

    /**
     * Indicates whether photographic evidence is mandatory when the item is OBSERVED or FAIL.
     */
    private boolean requiresEvidence;

    /**
     * Display order sequence for rendering the checklist on client applications.
     */
    private int displayOrder;

    /**
     * Operational state indicating whether this item is active in the current checklist catalog.
     */
    private boolean active;

    /**
     * Creation timestamp.
     */
    private Instant createdAt;

    /**
     * Last update timestamp.
     */
    private Instant updatedAt;

    /**
     * Constructs a new InspectionItem aggregate root.
     *
     * @param code             the standardized item code
     * @param name             the descriptive name
     * @param description      detailed instructions
     * @param category         the item category
     * @param system           the vehicle system
     * @param safetyComponent  whether it is a safety component
     * @param requiresEvidence whether evidence is mandatory on failure
     * @param displayOrder     presentation order
     */
    public InspectionItem(ItemCode code, ItemName name, String description,
                          ItemCategory category, ItemSystem system,
                          boolean safetyComponent, boolean requiresEvidence, int displayOrder) {
        this.id = new InspectionItemId();
        this.code = code;
        this.name = name;
        this.description = description != null ? description.strip() : "";
        this.category = category;
        this.system = system;
        this.safetyComponent = safetyComponent;
        this.requiresEvidence = requiresEvidence;
        this.displayOrder = displayOrder;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Reconstitutes an existing InspectionItem from persistence state.
     *
     * @param id               the unique identifier
     * @param code             the item code
     * @param name             the item name
     * @param description      the description
     * @param category         the item category
     * @param system           the vehicle system
     * @param safetyComponent  the safety component flag
     * @param requiresEvidence the requires evidence flag
     * @param displayOrder     the display order
     * @param active           the active status
     * @param createdAt        the creation timestamp
     * @param updatedAt        the last update timestamp
     * @return the reconstituted InspectionItem instance
     */
    public static InspectionItem restore(InspectionItemId id, ItemCode code, ItemName name,
                                         String description, ItemCategory category, ItemSystem system,
                                         boolean safetyComponent, boolean requiresEvidence,
                                         int displayOrder, boolean active, Instant createdAt, Instant updatedAt) {
        var item = new InspectionItem(code, name, description, category, system, safetyComponent, requiresEvidence, displayOrder);
        item.id = id;
        item.active = active;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;
        return item;
    }

    /**
     * Updates the catalog item specifications.
     *
     * @param name             the updated descriptive name
     * @param description      the updated instructions
     * @param category         the updated category
     * @param system           the updated vehicle system
     * @param safetyComponent  whether it is a safety component
     * @param requiresEvidence whether evidence is mandatory on failure
     * @param displayOrder     the updated display order
     */
    public void update(ItemName name, String description,
                       ItemCategory category, ItemSystem system,
                       boolean safetyComponent, boolean requiresEvidence, int displayOrder) {
        if (name == null) {
            throw new IllegalArgumentException("inspection.item.error.name.required");
        }
        if (category == null) {
            throw new IllegalArgumentException("inspection.item.error.category.required");
        }
        if (system == null) {
            throw new IllegalArgumentException("inspection.item.error.system.required");
        }
        this.name = name;
        this.description = description != null ? description.strip() : "";
        this.category = category;
        this.system = system;
        this.safetyComponent = safetyComponent;
        this.requiresEvidence = requiresEvidence;
        this.displayOrder = displayOrder;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the catalog item specifications.
     *
     * @param name             the updated descriptive name
     * @param description      detailed instructions
     * @param category         the updated category
     * @param system           the updated vehicle system
     * @param safetyComponent  whether it is a safety component
     * @param requiresEvidence whether evidence is mandatory on failure
     * @param displayOrder     the updated display order
     */
    public void update(ItemName name, String description,
                       ItemCategory category, ItemSystem system,
                       boolean safetyComponent, boolean requiresEvidence, int displayOrder) {
        if (name == null) {
            throw new IllegalArgumentException("inspection.item.error.name.required");
        }
        if (category == null) {
            throw new IllegalArgumentException("inspection.item.error.category.required");
        }
        if (system == null) {
            throw new IllegalArgumentException("inspection.item.error.system.required");
        }
        this.name = name;
        this.description = description != null ? description.strip() : "";
        this.category = category;
        this.system = system;
        this.safetyComponent = safetyComponent;
        this.requiresEvidence = requiresEvidence;
        this.displayOrder = displayOrder;
        this.updatedAt = Instant.now();
    }

    /**
     * Deactivates the item from the active inspection checklist catalog.
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    /**
     * Activates the item in the inspection checklist catalog.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }
}