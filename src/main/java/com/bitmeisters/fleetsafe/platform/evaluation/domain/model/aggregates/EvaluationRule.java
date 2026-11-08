package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class EvaluationRule extends AbstractDomainAggregateRoot<EvaluationRule> {

    private UUID id;
    private InspectionItemId inspectionItemId;
    private RuleName name;
    private ResultValue foundResult;
    private RuleImpact resultingImpact;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public EvaluationRule(DefineEvaluationRuleCommand command) {
        this.inspectionItemId = command.inspectionItemId();
        this.name = command.name();
        this.foundResult = command.foundResult();
        this.resultingImpact = command.resultingImpact();
        this.description = command.description();
        this.active = true;
    }

    public static EvaluationRule restore(UUID id, InspectionItemId inspectionItemId, RuleName name, ResultValue foundResult,
                                         RuleImpact resultingImpact, String description, boolean active,
                                         Instant createdAt, Instant updatedAt) {
        var rule = new EvaluationRule(new DefineEvaluationRuleCommand(inspectionItemId, name, foundResult, resultingImpact, description));
        rule.id = id;
        rule.active = active;
        rule.createdAt = createdAt;
        rule.updatedAt = updatedAt;
        return rule;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean appliesTo(InspectionItemId inspectionItemId, ResultValue result) {
        return active && this.inspectionItemId.equals(inspectionItemId) && this.foundResult == result;
    }
}
