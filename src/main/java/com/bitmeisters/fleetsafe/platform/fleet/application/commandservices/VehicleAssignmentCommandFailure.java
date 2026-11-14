package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

public sealed interface VehicleAssignmentCommandFailure
        permits VehicleAssignmentCommandFailure.VehicleNotFound,
                VehicleAssignmentCommandFailure.DriverNotFound,
                VehicleAssignmentCommandFailure.VehicleNotEnabled,
                VehicleAssignmentCommandFailure.VehicleAlreadyAssigned,
                VehicleAssignmentCommandFailure.DriverLicenseExpired,
                VehicleAssignmentCommandFailure.DriverAlreadyAssigned,
                VehicleAssignmentCommandFailure.NotFound,
                VehicleAssignmentCommandFailure.AlreadyClosed {

    String messageKey();

    record VehicleNotFound() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.vehicle-not-found";
        }
    }

    record DriverNotFound() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.driver-not-found";
        }
    }

    record VehicleNotEnabled() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.vehicle-not-enabled";
        }
    }

    record VehicleAlreadyAssigned() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.vehicle-already-assigned";
        }
    }

    record DriverLicenseExpired() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.driver-license-expired";
        }
    }

    record DriverAlreadyAssigned() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.driver-already-assigned";
        }
    }

    record NotFound() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.not-found";
        }
    }

    record AlreadyClosed() implements VehicleAssignmentCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.assignment.error.already-closed";
        }
    }
}
