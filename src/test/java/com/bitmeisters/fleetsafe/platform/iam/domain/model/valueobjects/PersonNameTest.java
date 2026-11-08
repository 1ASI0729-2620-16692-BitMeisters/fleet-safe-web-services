package com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonNameTest {

    @Test
    void trimsBothParts() {
        var name = new PersonName("  Rosa ", " Quispe  ");

        assertThat(name.fullName()).isEqualTo("Rosa Quispe");
    }

    @Test
    void requiresTheLastName() {
        assertThatThrownBy(() -> new PersonName("Rosa", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("iam.user.error.last-name.required");
    }

    @Test
    void rejectsANameLongerThanItsLimit() {
        assertThatThrownBy(() -> new PersonName("x".repeat(PersonName.MAX_LENGTH + 1), "Quispe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("iam.user.error.name.size");
    }
}
