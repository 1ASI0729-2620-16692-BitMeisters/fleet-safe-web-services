package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.InspectionResult;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.Observation;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.events.InspectionCompletedEvent;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionStatus;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.MediaType;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationDescription;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.Odometer;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Inspection aggregate root.
 *
 * <p>
 * Represents a pre-operational safety inspection conducted on a vehicle before beginning operations.
 * Encapsulates the entire evaluation lifecycle, verifies domain invariants, records results,
 * and registers the {@link InspectionCompletedEvent} when completed.
 * </p>
 */
@Getter
public class Inspection extends AbstractDomainAggregateRoot<Inspection> {

    /**
     * The unique identifier for the inspection.
     */
    private InspectionId id;

    /**
     * Reference to the inspected vehicle.
     */
    private VehicleId vehicleId;

    /**
     * Reference to the driver conducting the inspection.
     */
    private DriverId driverId;

    /**
     * The operational status of the inspection lifecycle.
     */
    private InspectionStatus status;

    /**
     * The odometer reading recorded at the start of the inspection.
     */
    private Odometer odometer;

    /**
     * The timestamp when the inspection started.
     */
    private Instant startedAt;

    /**
     * The timestamp when the inspection was marked as completed.
     */
    private Instant completedAt;

    /**
     * The collection of checklist evaluation results.
     */
    private final List<InspectionResult> results;

    /**
     * Creation timestamp.
     */
    private Instant createdAt;

    /**
     * Last update timestamp.
     */
    private Instant updatedAt;

    /**
     * Constructs a new Inspection aggregate root in progress.
     *
     * @param vehicleId the vehicle identifier
     * @param driverId  the driver identifier
     * @param odometer  the odometer reading
     */
    public Inspection(VehicleId vehicleId, DriverId driverId, Odometer odometer) {
        this.id = new InspectionId();
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.odometer = odometer;
        this.status = InspectionStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
        this.results = new ArrayList<>();
        this.createdAt = this.startedAt;
        this.updatedAt = this.startedAt;
    }

    /**
     * Reconstitutes an existing Inspection aggregate root from persistence state.
     *
     * @param id          the unique identifier
     * @param vehicleId   the vehicle identifier
     * @param driverId    the driver identifier
     * @param status      the inspection status
     * @param odometer    the odometer reading
     * @param startedAt   the start timestamp
     * @param completedAt the completion timestamp
     * @param results     the list of inspection results
     * @param createdAt   the creation timestamp
     * @param updatedAt   the update timestamp
     * @return the reconstituted Inspection instance
     */
    public static Inspection restore(InspectionId id, VehicleId vehicleId, DriverId driverId,
                                     InspectionStatus status, Odometer odometer, Instant startedAt,
                                     Instant completedAt, List<InspectionResult> results,
                                     Instant createdAt, Instant updatedAt) {
        var inspection = new Inspection(vehicleId, driverId, odometer);
        inspection.id = id;
        inspection.status = status;
        inspection.startedAt = startedAt;
        inspection.completedAt = completedAt;
        if (results != null) {
            inspection.results.addAll(results);
        }
        inspection.createdAt = createdAt;
        inspection.updatedAt = updatedAt;
        return inspection;
    }

    /**
     * Registers an evaluation result for a catalog checklist item.
     *
     * @param itemId       the catalog item identifier
     * @param itemName     the snapshot of the item name
     * @param itemCategory the snapshot of the item category
     * @param resultValue  the evaluation condition
     * @return the registered InspectionResult
     */
    public InspectionResult registerResult(InspectionItemId itemId, ItemName itemName,
                                           ItemCategory itemCategory, ResultValue resultValue) {
        verifyInProgress();
        boolean alreadyExists = results.stream().anyMatch(r -> r.getInspectionItemId().equals(itemId));
        if (alreadyExists) {
            throw new IllegalArgumentException("inspection.error.item-already-registered");
        }
        var result = new InspectionResult(itemId, itemName, itemCategory, resultValue);
        this.results.add(result);
        this.updatedAt = Instant.now();
        return result;
    }

    /**
     * Updates an existing evaluation result while the inspection is in progress.
     *
     * @param resultId  the result identifier
     * @param newResult the updated condition
     * @return the updated InspectionResult
     */
    public InspectionResult updateResult(InspectionResultId resultId, ResultValue newResult) {
        verifyInProgress();
        var result = findResultById(resultId);
        result.changeResult(newResult);
        this.updatedAt = Instant.now();
        return result;
    }

    /**
     * Adds an observation note to a specific inspection result.
     *
     * @param resultId    the result identifier
     * @param description the observation text
     * @param createdBy   the identifier of the author
     * @return the created Observation
     */
    public Observation addObservationToResult(InspectionResultId resultId,
                                              ObservationDescription description, UUID createdBy) {
        verifyInProgress();
        var result = findResultById(resultId);
        var observation = result.addObservation(description, createdBy);
        this.updatedAt = Instant.now();
        return observation;
    }

    /**
     * Attaches photographic evidence to an observation under a specific result.
     *
     * @param resultId      the result identifier
     * @param observationId the observation identifier
     * @param fileUrl       the URL of the stored file
     * @param mediaType     the media type
     */
    public void attachEvidenceToObservation(InspectionResultId resultId, ObservationId observationId,
                                            FileUrl fileUrl, MediaType mediaType) {
        verifyInProgress();
        var result = findResultById(resultId);
        result.attachEvidenceToObservation(observationId, fileUrl, mediaType);
        this.updatedAt = Instant.now();
    }

    /**
     * Completes the inspection and triggers domain evaluation events.
     *
     * @param activeCatalogItems the list of all currently active checklist items
     */
    public void complete(List<InspectionItem> activeCatalogItems) {
        verifyInProgress();

        // Invariant 1: Every active catalog item must be evaluated
        for (var item : activeCatalogItems) {
            boolean evaluated = results.stream()
                    .anyMatch(r -> r.getInspectionItemId().equals(item.getId()));
            if (!evaluated) {
                throw new IllegalArgumentException("inspection.complete.error.missing-items");
            }
        }

        // Invariant 2 & 3: Validate mandatory observations and evidences for anomalies
        for (var res : results) {
            if (res.getResult() == ResultValue.OBSERVED || res.getResult() == ResultValue.FAIL) {
                if (!res.hasObservations()) {
                    throw new IllegalArgumentException("inspection.complete.error.missing-observation");
                }
                var catalogItem = activeCatalogItems.stream()
                        .filter(item -> item.getId().equals(res.getInspectionItemId()))
                        .findFirst();
                if (catalogItem.isPresent() && catalogItem.get().isRequiresEvidence() && !res.hasEvidences()) {
                    throw new IllegalArgumentException("inspection.complete.error.missing-evidence");
                }
            }
        }

        this.status = InspectionStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.updatedAt = this.completedAt;

        // Register domain event for cross-context consumption
        registerDomainEvent(new InspectionCompletedEvent(this));
    }

    private void verifyInProgress() {
        if (this.status != InspectionStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("inspection.error.not-in-progress");
        }
    }

    private InspectionResult findResultById(InspectionResultId resultId) {
        return results.stream()
                .filter(r -> r.getId().equals(resultId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("inspection.error.result-not-found"));
    }

    /**
     * Returns an unmodifiable list of results.
     *
     * @return list of inspection results
     */
    public List<InspectionResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    /**
     * Counts the total number of items evaluated as OK.
     *
     * @return count of compliant results
     */
    public int getCompliantCount() {
        return (int) results.stream().filter(r -> r.getResult() == ResultValue.OK).count();
    }

    /**
     * Counts the total number of items evaluated as OBSERVED.
     *
     * @return count of observed results
     */
    public int getObservedCount() {
        return (int) results.stream().filter(r -> r.getResult() == ResultValue.OBSERVED).count();
    }

    /**
     * Counts the total number of items evaluated as FAIL.
     *
     * @return count of failed results
     */
    public int getFailedCount() {
        return (int) results.stream().filter(r -> r.getResult() == ResultValue.FAIL).count();
    }
}