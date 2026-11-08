package com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.seeding;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class DemoDataSeederTest {

    private final DataSource dataSource = mock(DataSource.class);

    @Test
    void loadsNothingUnlessItIsEnabled() throws Exception {
        new DemoDataSeeder(dataSource, false, "8b1e5c4a-2f3d-4e6a-9b7c-1d2e3f4a5b6c").seed();

        verifyNoInteractions(dataSource);
    }

    @Test
    void loadsNothingWithoutTheCompanyOfTheAdministrator() throws Exception {
        new DemoDataSeeder(dataSource, true, "").seed();

        verifyNoInteractions(dataSource);
    }

    @Test
    void rejectsACompanyThatIsNotAnIdentifier() {
        assertThatThrownBy(() -> new DemoDataSeeder(dataSource, true, "COM001").seed())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
