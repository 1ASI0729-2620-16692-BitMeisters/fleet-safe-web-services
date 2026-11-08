package com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailAddressTest {

    @Test
    void isStoredTrimmedAndInLowerCase() {
        assertThat(new EmailAddress("  Supervisor@TransportesAndinos.PE ").value())
                .isEqualTo("supervisor@transportesandinos.pe");
    }

    @Test
    void twoSpellingsOfTheSameAddressAreEqual() {
        assertThat(new EmailAddress("ROSA@andinos.pe")).isEqualTo(new EmailAddress("rosa@andinos.pe"));
    }

    @Test
    void rejectsAnAddressWithoutDomain() {
        assertThatThrownBy(() -> new EmailAddress("rosa@"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("iam.user.error.email.format");
    }

    @Test
    void rejectsABlankAddress() {
        assertThatThrownBy(() -> new EmailAddress(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("iam.user.error.email.required");
    }
}
