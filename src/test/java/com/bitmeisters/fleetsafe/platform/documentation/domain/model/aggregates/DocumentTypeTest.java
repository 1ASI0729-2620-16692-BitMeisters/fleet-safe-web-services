package com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterDocumentTypeCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentTypeTest {

    @Test
    void aRequiredActiveTypeIsMandatoryForOperation() {
        var soat = new DocumentType(new RegisterDocumentTypeCommand(new DocumentTypeCode("SOAT"),
                new DocumentTypeName("Mandatory traffic accident insurance"), null, true));

        assertThat(soat.isActive()).isTrue();
        assertThat(soat.isMandatoryForOperation()).isTrue();
    }

    @Test
    void anOptionalTypeIsNotMandatory() {
        var photo = new DocumentType(new RegisterDocumentTypeCommand(new DocumentTypeCode("PHOTO"),
                new DocumentTypeName("Vehicle photo"), null, false));

        assertThat(photo.isMandatoryForOperation()).isFalse();
    }

    @Test
    void anInactiveTypeIsNotMandatory() {
        var now = Instant.parse("2026-10-28T14:00:00Z");
        var soat = DocumentType.restore(UUID.randomUUID(), new DocumentTypeCode("SOAT"),
                new DocumentTypeName("Mandatory traffic accident insurance"), null, true, false, now, now);

        assertThat(soat.isMandatoryForOperation()).isFalse();
    }

    @Test
    void rejectsANameLongerThanItsLimit() {
        assertThatThrownBy(() -> new DocumentTypeName("x".repeat(DocumentTypeName.MAX_LENGTH + 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("documentation.type.error.name.size");
    }
}
