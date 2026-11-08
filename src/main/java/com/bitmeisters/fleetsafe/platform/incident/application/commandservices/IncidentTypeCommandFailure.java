package com.bitmeisters.fleetsafe.platform.incident.application.commandservices;

public sealed interface IncidentTypeCommandFailure
        permits IncidentTypeCommandFailure.NotFound,
        IncidentTypeCommandFailure.DuplicateCode {

    String messageKey();

    record NotFound() implements IncidentTypeCommandFailure {
        @Override
        public String messageKey() {
            return "incident.type.error.not-found";
        }
    }

    record DuplicateCode() implements IncidentTypeCommandFailure {
        @Override
        public String messageKey() {
            return "incident.type.error.duplicate-code";
        }
    }
}