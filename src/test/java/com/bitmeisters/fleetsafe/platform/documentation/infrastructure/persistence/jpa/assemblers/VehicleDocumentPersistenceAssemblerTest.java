package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentStatus;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeName;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.ValidityPeriod;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleDocumentPersistenceAssemblerTest {

    private final Instant created = Instant.parse("2026-10-28T14:00:00Z");

    @Test
    void keepsEveryFieldOfADocumentWhenGoingToTheDatabaseAndBack() {
        var document = VehicleDocument.restore(UUID.randomUUID(), new VehicleId(UUID.randomUUID()), new DocumentTypeId(UUID.randomUUID()),
                new DocumentNumber("SOAT-2026-004512"), new ValidityPeriod(LocalDate.of(2026, 1, 15), LocalDate.of(2027, 1, 14)),
                DocumentStatus.VALID, new FileUrl("https://storage.example.com/soat.pdf"), created, created);

        var restored = VehicleDocumentPersistenceAssembler.toDomainFromPersistence(
                VehicleDocumentPersistenceAssembler.toPersistenceFromDomain(document));

        assertThat(restored).usingRecursiveComparison().ignoringFields("updatedAt").isEqualTo(document);
    }

    @Test
    void keepsADocumentWithoutLink() {
        var document = VehicleDocument.restore(UUID.randomUUID(), new VehicleId(UUID.randomUUID()), new DocumentTypeId(UUID.randomUUID()),
                new DocumentNumber("TP-88231"), new ValidityPeriod(LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 1)),
                DocumentStatus.EXPIRING, null, created, created);

        var restored = VehicleDocumentPersistenceAssembler.toDomainFromPersistence(
                VehicleDocumentPersistenceAssembler.toPersistenceFromDomain(document));

        assertThat(restored.getFileUrl()).isNull();
        assertThat(restored.getStatus()).isEqualTo(DocumentStatus.EXPIRING);
    }

    @Test
    void keepsEveryFieldOfADocumentTypeWhenGoingToTheDatabaseAndBack() {
        var documentType = DocumentType.restore(UUID.randomUUID(), new DocumentTypeCode("SOAT"),
                new DocumentTypeName("Mandatory traffic accident insurance"), "Required to circulate", true, false, created, created);

        var restored = DocumentTypePersistenceAssembler.toDomainFromPersistence(
                DocumentTypePersistenceAssembler.toPersistenceFromDomain(documentType));

        assertThat(restored).usingRecursiveComparison().ignoringFields("updatedAt").isEqualTo(documentType);
    }
}
