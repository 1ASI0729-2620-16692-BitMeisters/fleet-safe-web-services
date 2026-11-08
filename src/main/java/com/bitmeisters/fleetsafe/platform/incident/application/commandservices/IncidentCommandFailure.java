package com.bitmeisters.fleetsafe.platform.incident.application.commandservices;

public sealed interface IncidentCommandFailure
        permits IncidentCommandFailure.NotFound,
        IncidentCommandFailure.UnknownVehicle,
        IncidentCommandFailure.UnknownIncidentType,
        IncidentCommandFailure.UnknownInspection,
        IncidentCommandFailure.InvalidStatus {

    String messageKey();

    record NotFound() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "incident.error.not-found";
        }
    }

    record UnknownVehicle() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "incident.error.unknown-vehicle";
        }
    }

    record UnknownIncidentType() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "incident.error.unknown-incident-type";
        }
    }

    record UnknownInspection() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "incident.error.unknown-inspection";
        }
    }

    record InvalidStatus() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "incident.error.invalid-status";
        }
    }
}