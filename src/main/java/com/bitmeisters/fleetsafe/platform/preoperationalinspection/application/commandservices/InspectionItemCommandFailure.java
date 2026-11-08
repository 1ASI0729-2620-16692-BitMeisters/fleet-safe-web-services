package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices;

/**
 * Sealed interface representing failure outcomes when executing inspection item commands.
 */
public sealed interface InspectionItemCommandFailure
        permits InspectionItemCommandFailure.DuplicateCode,
                InspectionItemCommandFailure.NotFound,
                InspectionItemCommandFailure.AlreadyInactive {

    /**
     * Resolves the internationalization message key associated with this failure.
     *
     * @return message bundle key
     */
    String messageKey();

    /**
     * Failure when an inspection item with the specified code already exists.
     */
    record DuplicateCode() implements InspectionItemCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.item.error.duplicate-code";
        }
    }

    /**
     * Failure when the requested inspection item was not found.
     */
    record NotFound() implements InspectionItemCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.item.error.not-found";
        }
    }

    /**
     * Failure when attempting to deactivate an item that is already inactive.
     */
    record AlreadyInactive() implements InspectionItemCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.item.error.already-inactive";
        }
    }
}
