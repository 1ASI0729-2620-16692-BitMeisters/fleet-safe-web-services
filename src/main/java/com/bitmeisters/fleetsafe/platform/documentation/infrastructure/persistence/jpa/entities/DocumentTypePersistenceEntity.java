package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeName;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_types")
@Getter
@Setter
@NoArgsConstructor
public class DocumentTypePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "code", nullable = false, unique = true, length = DocumentTypeCode.MAX_LENGTH)
    private String code;

    @Column(name = "name", nullable = false, length = DocumentTypeName.MAX_LENGTH)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
