package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.EvidenceId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.MediaType;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationDescription;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationId;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Observation domain entity.
 *
 * <p>
 * Represents a descriptive finding recorded by a driver or fleet supervisor
 * regarding an observed anomaly or safety failure found during an inspection.
 * </p>
 */
@Getter
public class Observation {

    /**
     * The unique identifier for the observation.
     */
    private final ObservationId id;

    /**
     * Detailed narrative description of the detected condition.
     */
    private final ObservationDescription description;

    /**
     * The identifier of the user who recorded the observation.
     */
    private final UUID createdBy;

    /**
     * The timestamp when the observation was created.
     */
    private final Instant createdAt;

    /**
     * The collection of photographic evidences attached to this observation.
     */
    private final List<Evidence> evidences;

    /**
     * Constructs a new Observation instance.
     *
     * @param description the description of the finding
     * @param createdBy   the identifier of the author
     */
    public Observation(ObservationDescription description, UUID createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("inspection.observation.error.created-by.required");
        }
        this.id = new ObservationId();
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
        this.evidences = new ArrayList<>();
    }

    /**
     * Reconstitutes an existing Observation entity from persistence state.
     *
     * @param id          the unique identifier
     * @param description the description of the finding
     * @param createdBy   the author identifier
     * @param createdAt   the creation timestamp
     * @param evidences   the attached evidences
     * @return the reconstituted Observation instance
     */
    public static Observation restore(ObservationId id, ObservationDescription description, UUID createdBy,
                                      Instant createdAt, List<Evidence> evidences) {
        var observation = new Observation(id, description, createdBy, createdAt);
        if (evidences != null) {
            observation.evidences.addAll(evidences);
        }
        return observation;
    }

    private Observation(ObservationId id, ObservationDescription description, UUID createdBy, Instant createdAt) {
        this.id = id;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.evidences = new ArrayList<>();
    }

    /**
     * Attaches photographic evidence to this observation.
     *
     * @param fileUrl   the URL where the evidence is hosted
     * @param mediaType the MIME media type
     */
    public void attachEvidence(FileUrl fileUrl, MediaType mediaType) {
        this.evidences.add(new Evidence(fileUrl, mediaType));
    }

    /**
     * Returns an unmodifiable view of the attached evidences.
     *
     * @return list of evidences
     */
    public List<Evidence> getEvidences() {
        return Collections.unmodifiableList(evidences);
    }
}