package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities.EvaluationRulePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationRulePersistenceRepository extends JpaRepository<EvaluationRulePersistenceEntity, UUID> {

    List<EvaluationRulePersistenceEntity> findAllByActiveTrue();

    boolean existsByInspectionItemIdAndFoundResultAndActiveTrue(UUID inspectionItemId, ResultValue foundResult);
}
