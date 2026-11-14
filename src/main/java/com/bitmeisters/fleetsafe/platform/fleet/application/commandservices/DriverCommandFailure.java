package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

public sealed interface DriverCommandFailure
        permits DriverCommandFailure.UserNotFound,
                DriverCommandFailure.DuplicateDriver {

    String messageKey();

    record UserNotFound() implements DriverCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.driver.error.user-not-found";
        }
    }

    record DuplicateDriver() implements DriverCommandFailure {
        @Override
        public String messageKey() {
            return "fleet.driver.error.duplicate";
        }
    }
}
