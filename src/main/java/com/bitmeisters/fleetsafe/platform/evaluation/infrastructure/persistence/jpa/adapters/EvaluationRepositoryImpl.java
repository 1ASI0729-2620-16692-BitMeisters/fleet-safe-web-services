package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRepository;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers.EvaluationPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.repositories.EvaluationPersistenceRepository;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EvaluationRepositoryImpl implements EvaluationRepository {

    private final EvaluationPersistenceRepository evaluationPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public EvaluationRepositoryImpl(EvaluationPersistenceRepository evaluationPersistenceRepository,
                                    DomainEventPublisher domainEventPublisher) {
        this.evaluationPersistenceRepository = evaluationPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public boolean existsByInspectionId(InspectionId inspectionId) {
        return evaluationPersistenceRepository.existsByInspectionId(inspectionId.value());
    }

    @Override
    public Optional<Evaluation> findByInspectionId(InspectionId inspectionId) {
        return evaluationPersistenceRepository.findByInspectionId(inspectionId.value())
                .map(EvaluationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Evaluation save(Evaluation evaluation) {
        var saved = evaluationPersistenceRepository.save(EvaluationPersistenceAssembler.toPersistenceFromDomain(evaluation));
        domainEventPublisher.publishAndClearEvents(evaluation);
        return EvaluationPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
