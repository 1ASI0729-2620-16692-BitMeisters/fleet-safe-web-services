package com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices;

public sealed interface EvaluationRuleCommandFailure
        permits EvaluationRuleCommandFailure.Duplicate,
                EvaluationRuleCommandFailure.NotFound,
                EvaluationRuleCommandFailure.AlreadyInactive {

    String messageKey();

    record Duplicate() implements EvaluationRuleCommandFailure {
        @Override
        public String messageKey() {
            return "evaluation.rule.error.duplicate";
        }
    }

    record NotFound() implements EvaluationRuleCommandFailure {
        @Override
        public String messageKey() {
            return "evaluation.rule.error.not-found";
        }
    }

    record AlreadyInactive() implements EvaluationRuleCommandFailure {
        @Override
        public String messageKey() {
            return "evaluation.rule.error.already-inactive";
        }
    }
}
