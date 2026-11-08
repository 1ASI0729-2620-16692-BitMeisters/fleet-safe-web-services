package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices;

/**
 * Sealed interface representing failure outcomes when executing inspection commands.
 */
public sealed interface InspectionCommandFailure
        permits InspectionCommandFailure.NotFound,
                InspectionCommandFailure.NotInProgress,
                InspectionCommandFailure.ItemAlreadyRegistered,
                InspectionCommandFailure.ResultNotFound,
                InspectionCommandFailure.ObservationNotFound,
                InspectionCommandFailure.MissingItems,
                InspectionCommandFailure.MissingObservation,
                InspectionCommandFailure.MissingEvidence,
                InspectionCommandFailure.VehicleHasOngoingInspection,
                InspectionCommandFailure.ItemNotFound {

    /**
     * Resolves the internationalization message key associated with this failure.
     *
     * @return message bundle key
     */
    String messageKey();

    /**
     * The requested inspection does not exist.
     */
    record NotFound() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.error.not-found";
        }
    }

    /**
     * The inspection is already completed and no longer in progress.
     */
    record NotInProgress() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.error.not-in-progress";
        }
    }

    /**
     * A result for this checklist item has already been registered in this inspection.
     */
    record ItemAlreadyRegistered() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.error.item-already-registered";
        }
    }

    /**
     * The inspection result was not found.
     */
    record ResultNotFound() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.error.result-not-found";
        }
    }

    /**
     * The observation was not found.
     */
    record ObservationNotFound() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.error.observation-not-found";
        }
    }

    /**
     * Not all active checklist items have been evaluated.
     */
    record MissingItems() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.complete.error.missing-items";
        }
    }

    /**
     * Results with OBSERVED or FAIL require at least one observation note.
     */
    record MissingObservation() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.complete.error.missing-observation";
        }
    }

    /**
     * Checklist items requiring evidence on anomaly must include at least one photo.
     */
    record MissingEvidence() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.complete.error.missing-evidence";
        }
    }

    /**
     * The vehicle already has an ongoing inspection in progress.
     */
    record VehicleHasOngoingInspection() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.error.vehicle-has-ongoing-inspection";
        }
    }

    /**
     * The referenced checklist item does not exist in the catalog.
     */
    record ItemNotFound() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.item.error.not-found";
        }
    }
}
