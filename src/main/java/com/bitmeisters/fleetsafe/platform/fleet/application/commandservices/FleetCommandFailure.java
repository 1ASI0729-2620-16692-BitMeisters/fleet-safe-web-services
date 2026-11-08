package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

public sealed interface FleetCommandFailure
        permits FleetCommandFailure.CompanyNotRegistered {

    String messageKey();

    record CompanyNotRegistered() implements FleetCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.error.company-not-registered";
        }
    }
}
