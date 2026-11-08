package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.entities.DocumentTypePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentTypePersistenceRepository extends JpaRepository<DocumentTypePersistenceEntity, UUID> {

    boolean existsByCode(String code);
}
