package com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices;

public sealed interface EvaluationCommandFailure
        permits EvaluationCommandFailure.AlreadyEvaluated,
                EvaluationCommandFailure.NothingToEvaluate {

    String messageKey();

    record AlreadyEvaluated() implements EvaluationCommandFailure {
        @Override
        public String messageKey() {
            return "evaluation.error.already-evaluated";
        }
    }

    record NothingToEvaluate() implements EvaluationCommandFailure {
        @Override
        public String messageKey() {
            return "evaluation.error.nothing-to-evaluate";
        }
    }
}
