package com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices;

public sealed interface OperationalAuthorizationCommandFailure
        permits OperationalAuthorizationCommandFailure.NotFound,
                OperationalAuthorizationCommandFailure.NotBlocked {

    String messageKey();

    record NotFound() implements OperationalAuthorizationCommandFailure {
        @Override
        public String messageKey() {
            return "evaluation.authorization.error.not-found";
        }
    }

    record NotBlocked() implements OperationalAuthorizationCommandFailure {
        @Override
        public String messageKey() {
            return "evaluation.authorization.error.not-blocked";
        }
    }
}
