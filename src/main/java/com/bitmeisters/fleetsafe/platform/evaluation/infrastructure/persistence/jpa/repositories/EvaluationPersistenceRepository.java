package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities.EvaluationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvaluationPersistenceRepository extends JpaRepository<EvaluationPersistenceEntity, UUID> {

    boolean existsByInspectionId(UUID inspectionId);

    Optional<EvaluationPersistenceEntity> findByInspectionId(UUID inspectionId);
}
