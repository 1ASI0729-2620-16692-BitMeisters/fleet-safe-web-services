package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.Timestamps;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.entities.EvaluationDetail;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultSnapshot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Evaluation extends AbstractDomainAggregateRoot<Evaluation> {

    private UUID id;
    private InspectionId inspectionId;
    private Instant evaluatedAt;
    private UUID evaluatedBy;
    private final List<EvaluationDetail> details = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    private Evaluation(InspectionId inspectionId, Instant evaluatedAt, UUID evaluatedBy) {
        this.inspectionId = inspectionId;
        this.evaluatedAt = evaluatedAt;
        this.evaluatedBy = evaluatedBy;
    }

    public Evaluation(EvaluateInspectionCommand command, List<EvaluationRule> activeRules) {
        this(command.inspectionId(), Timestamps.now(), command.evaluatedBy());
        command.results().forEach(result -> applyMatchingRule(result, activeRules));
    }

    public static Evaluation restore(UUID id, InspectionId inspectionId, Instant evaluatedAt, UUID evaluatedBy,
                                     List<EvaluationDetail> details, Instant createdAt, Instant updatedAt) {
        var evaluation = new Evaluation(inspectionId, evaluatedAt, evaluatedBy);
        evaluation.id = id;
        evaluation.details.addAll(details);
        evaluation.createdAt = createdAt;
        evaluation.updatedAt = updatedAt;
        return evaluation;
    }

    private void applyMatchingRule(InspectionResultSnapshot result, List<EvaluationRule> activeRules) {
        activeRules.stream()
                .filter(rule -> rule.appliesTo(result.inspectionItemId(), result.value()))
                .findFirst()
                .ifPresent(rule -> details.add(new EvaluationDetail(
                        result.inspectionResultId(), rule.getId(), rule.getResultingImpact())));
    }

    public RuleImpact resolveMostRestrictiveImpact() {
        return details.stream()
                .map(EvaluationDetail::getAppliedImpact)
                .reduce(RuleImpact.NONE, RuleImpact::mostRestrictive);
    }

    public boolean isAutomatic() {
        return evaluatedBy == null;
    }

    public List<EvaluationDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }
}
