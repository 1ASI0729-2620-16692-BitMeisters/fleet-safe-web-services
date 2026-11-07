package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

public sealed interface CompanyCommandFailure
        permits CompanyCommandFailure.AlreadyRegistered,
                CompanyCommandFailure.DuplicateTaxId {

    String messageKey();

    record AlreadyRegistered() implements CompanyCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.company.error.already-registered";
        }
    }

    record DuplicateTaxId() implements CompanyCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.company.error.tax-id.duplicate";
        }
    }
}
