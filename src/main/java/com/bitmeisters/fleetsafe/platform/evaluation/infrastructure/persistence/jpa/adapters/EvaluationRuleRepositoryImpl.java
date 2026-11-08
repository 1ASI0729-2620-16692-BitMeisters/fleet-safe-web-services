package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRuleRepository;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers.EvaluationRulePersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.repositories.EvaluationRulePersistenceRepository;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EvaluationRuleRepositoryImpl implements EvaluationRuleRepository {

    private final EvaluationRulePersistenceRepository evaluationRulePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public EvaluationRuleRepositoryImpl(EvaluationRulePersistenceRepository evaluationRulePersistenceRepository,
                                        DomainEventPublisher domainEventPublisher) {
        this.evaluationRulePersistenceRepository = evaluationRulePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<EvaluationRule> findById(UUID id) {
        return evaluationRulePersistenceRepository.findById(id).map(EvaluationRulePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<EvaluationRule> findAllByActiveTrue() {
        return evaluationRulePersistenceRepository.findAllByActiveTrue().stream()
                .map(EvaluationRulePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByInspectionItemIdAndFoundResultAndActiveTrue(InspectionItemId inspectionItemId, ResultValue foundResult) {
        return evaluationRulePersistenceRepository.existsByInspectionItemIdAndFoundResultAndActiveTrue(inspectionItemId.value(), foundResult);
    }

    @Override
    public EvaluationRule save(EvaluationRule evaluationRule) {
        var saved = evaluationRulePersistenceRepository.save(EvaluationRulePersistenceAssembler.toPersistenceFromDomain(evaluationRule));
        domainEventPublisher.publishAndClearEvents(evaluationRule);
        return EvaluationRulePersistenceAssembler.toDomainFromPersistence(saved);
    }
}
