package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.EvidenceId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.MediaType;
import lombok.Getter;

import java.time.Instant;

/**
 * Evidence domain entity.
 *
 * <p>
 * Represents digital or photographic evidence attached to an observation during an inspection.
 * Backs up and verifies a detected vehicle condition.
 * </p>
 */
@Getter
public class Evidence {

    /**
     * The unique identifier for the evidence.
     */
    private final EvidenceId id;

    /**
     * The secure URL where the evidence file is hosted.
     */
    private final FileUrl fileUrl;

    /**
     * The media type of the uploaded evidence.
     */
    private final MediaType mediaType;

    /**
     * The timestamp when the evidence was uploaded.
     */
    private final Instant uploadedAt;

    /**
     * Constructs a new Evidence instance with a generated identifier and current timestamp.
     *
     * @param fileUrl   the URL pointing to the uploaded file
     * @param mediaType the media format of the file
     */
    public Evidence(FileUrl fileUrl, MediaType mediaType) {
        this.fileUrl = fileUrl;
        this.mediaType = mediaType;
        this.uploadedAt = Instant.now();
    }

    /**
     * Reconstitutes an existing Evidence entity from persistence state.
     *
     * @param id         the unique identifier
     * @param fileUrl    the file URL
     * @param mediaType  the media type
     * @param uploadedAt the upload timestamp
     * @return the reconstituted Evidence instance
     */
    public static Evidence restore(EvidenceId id, FileUrl fileUrl, MediaType mediaType, Instant uploadedAt) {
        return new Evidence(id, fileUrl, mediaType, uploadedAt);
    }

    private Evidence(EvidenceId id, FileUrl fileUrl, MediaType mediaType, Instant uploadedAt) {
        this.id = id;
        this.fileUrl = fileUrl;
        this.mediaType = mediaType;
        this.uploadedAt = uploadedAt;
    }
}