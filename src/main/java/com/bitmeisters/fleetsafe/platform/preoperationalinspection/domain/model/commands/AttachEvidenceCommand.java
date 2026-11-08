package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.MediaType;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationId;

/**
 * Command to attach photographic evidence to an observation under an inspection result.
 */
public record AttachEvidenceCommand(
        InspectionId inspectionId,
        InspectionResultId resultId,
        ObservationId observationId,
        FileUrl fileUrl,
        MediaType mediaType) {

    public AttachEvidenceCommand {
        if (inspectionId == null) {
            throw new IllegalArgumentException("inspection.error.id.required");
        }
        if (resultId == null) {
            throw new IllegalArgumentException("inspection.result.error.id.required");
        }
        if (observationId == null) {
            throw new IllegalArgumentException("inspection.observation.error.id.required");
        }
        if (fileUrl == null) {
            throw new IllegalArgumentException("inspection.evidence.error.file-url.required");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("inspection.evidence.error.media-type.unsupported");
        }
    }
}
