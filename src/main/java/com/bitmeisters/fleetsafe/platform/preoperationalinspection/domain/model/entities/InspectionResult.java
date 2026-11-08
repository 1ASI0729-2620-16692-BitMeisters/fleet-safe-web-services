package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.EvidenceId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.MediaType;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationDescription;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ResultValue;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * InspectionResult domain entity.
 *
 * <p>
 * Represents the evaluation outcome of an individual checklist item.
 * Encapsulates the assigned condition (OK, OBSERVED, or FAIL), item metadata snapshots,
 * and associated observations.
 * </p>
 */
@Getter
public class InspectionResult {

    /**
     * The unique identifier for the inspection result.
     */
    private final InspectionResultId id;

    /**
     * The identifier of the evaluated catalog item.
     */
    private final InspectionItemId inspectionItemId;

    /**
     * Snapshot of the item name at the time of evaluation.
     */
    private final ItemName itemName;

    /**
     * Snapshot of the item category at the time of evaluation.
     */
    private final ItemCategory itemCategory;

    /**
     * The condition recorded for this item.
     */
    private ResultValue result;

    /**
     * The timestamp when this result was registered.
     */
    private final Instant createdAt;

    /**
     * The list of observations explaining anomalies or failures.
     */
    private final List<Observation> observations;

    /**
     * Constructs a new InspectionResult instance.
     *
     * @param inspectionItemId the item identifier
     * @param itemName         snapshot of the item name
     * @param itemCategory     snapshot of the item category
     * @param result           the evaluation outcome
     */
    public InspectionResult(InspectionItemId inspectionItemId, ItemName itemName,
                            ItemCategory itemCategory, ResultValue result) {
        this.id = null;
        if (result == null) {
            throw new IllegalArgumentException("inspection.result.error.result-value.required");
        }
        this.inspectionItemId = inspectionItemId;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.result = result;
        this.createdAt = Instant.now();
        this.observations = new ArrayList<>();
    }

    /**
     * Reconstitutes an existing InspectionResult entity from persistence state.
     *
     * @param id               the unique identifier
     * @param inspectionItemId the item identifier
     * @param itemName         the item name
     * @param itemCategory     the item category
     * @param result           the result value
     * @param createdAt        the creation timestamp
     * @param observations     the associated observations
     * @return the reconstituted InspectionResult instance
     */
    public static InspectionResult restore(InspectionResultId id, InspectionItemId inspectionItemId,
                                           ItemName itemName, ItemCategory itemCategory,
                                           ResultValue result, Instant createdAt, List<Observation> observations) {
        var inspectionResult = new InspectionResult(id, inspectionItemId, itemName, itemCategory, result, createdAt);
        if (observations != null) {
            inspectionResult.observations.addAll(observations);
        }
        return inspectionResult;
    }

    private InspectionResult(InspectionResultId id, InspectionItemId inspectionItemId,
                             ItemName itemName, ItemCategory itemCategory,
                             ResultValue result, Instant createdAt) {
        this.id = id;
        this.inspectionItemId = inspectionItemId;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.result = result;
        this.createdAt = createdAt;
        this.observations = new ArrayList<>();
    }

    /**
     * Updates the evaluation outcome of this result.
     *
     * @param newResult the new result value
     */
    public void changeResult(ResultValue newResult) {
        if (newResult == null) {
            throw new IllegalArgumentException("inspection.result.error.result-value.required");
        }
        this.result = newResult;
    }

    /**
     * Adds an observation describing an anomaly or failure.
     *
     * @param description the narrative description
     * @param createdBy   the identifier of the author
     * @return the created Observation instance
     */
    public Observation addObservation(ObservationDescription description, UUID createdBy) {
        var observation = new Observation(description, createdBy);
        this.observations.add(observation);
        return observation;
    }

    /**
     * Attaches photographic evidence to a specific observation under this result.
     *
     * @param observationId the identifier of the observation
     * @param fileUrl       the URL where the evidence is stored
     * @param mediaType     the media format
     */
    public void attachEvidenceToObservation(ObservationId observationId, FileUrl fileUrl, MediaType mediaType) {
        var observation = observations.stream()
                .filter(obs -> observationId.equals(obs.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("inspection.error.observation-not-found"));
        observation.attachEvidence(fileUrl, mediaType);
    }

    /**
     * Returns an unmodifiable list of observations.
     *
     * @return list of observations
     */
    public List<Observation> getObservations() {
        return Collections.unmodifiableList(observations);
    }

    /**
     * Verifies whether at least one observation is registered.
     *
     * @return true if observations exist, false otherwise
     */
    public boolean hasObservations() {
        return !observations.isEmpty();
    }

    /**
     * Verifies whether at least one attached photographic evidence exists.
     *
     * @return true if evidence exists, false otherwise
     */
    public boolean hasEvidences() {
        return observations.stream().anyMatch(obs -> !obs.getEvidences().isEmpty());
    }
}