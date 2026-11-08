package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.MediaType;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA Persistence entity for photographic evidences attached to inspection observations.
 */
@Entity
@Table(name = "inspection_evidences")
@Getter
@Setter
@NoArgsConstructor
public class EvidencePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "observation_id", nullable = false)
    private ObservationPersistenceEntity observation;

    @Column(name = "file_url", nullable = false, length = FileUrl.MAX_LENGTH)
    private String fileUrl;

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;
}
