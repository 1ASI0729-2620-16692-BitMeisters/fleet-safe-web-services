package com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects;

public record PersonName(String firstName, String lastName) {

    public static final int MAX_LENGTH = 100;

    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("iam.user.error.first-name.required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("iam.user.error.last-name.required");
        }
        firstName = firstName.strip();
        lastName = lastName.strip();
        if (firstName.length() > MAX_LENGTH || lastName.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("iam.user.error.name.size");
        }
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}
