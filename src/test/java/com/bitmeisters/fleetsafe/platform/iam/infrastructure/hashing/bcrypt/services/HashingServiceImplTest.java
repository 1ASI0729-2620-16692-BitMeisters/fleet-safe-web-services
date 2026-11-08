package com.bitmeisters.fleetsafe.platform.iam.infrastructure.hashing.bcrypt.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashingServiceImplTest {

    private final HashingServiceImpl hashingService = new HashingServiceImpl();

    @Test
    void neverReturnsThePasswordItself() {
        assertThat(hashingService.encode("S3cure!pass")).isNotEqualTo("S3cure!pass").startsWith("$2a$");
    }

    @Test
    void producesADifferentHashEachTimeForTheSamePassword() {
        assertThat(hashingService.encode("S3cure!pass")).isNotEqualTo(hashingService.encode("S3cure!pass"));
    }

    @Test
    void recognisesTheRightPasswordAndRejectsAWrongOne() {
        var hash = hashingService.encode("S3cure!pass");

        assertThat(hashingService.matches("S3cure!pass", hash)).isTrue();
        assertThat(hashingService.matches("s3cure!pass", hash)).isFalse();
    }
}
