package com.bitmeisters.fleetsafe.platform.documentation.application.commandservices;

public sealed interface VehicleDocumentCommandFailure
        permits VehicleDocumentCommandFailure.NotFound,
                VehicleDocumentCommandFailure.UnknownDocumentType {

    String messageKey();

    record NotFound() implements VehicleDocumentCommandFailure {
        @Override
        public String messageKey() {
            return "documentation.document.error.not-found";
        }
    }

    record UnknownDocumentType() implements VehicleDocumentCommandFailure {
        @Override
        public String messageKey() {
            return "documentation.document.error.unknown-document-type";
        }
    }
}
