package com.bitmeisters.fleetsafe.platform.documentation.application.commandservices;

public sealed interface DocumentTypeCommandFailure
        permits DocumentTypeCommandFailure.DuplicateCode {

    String messageKey();

    record DuplicateCode() implements DocumentTypeCommandFailure {
        @Override
        public String messageKey() {
            return "documentation.type.error.duplicate-code";
        }
    }
}
