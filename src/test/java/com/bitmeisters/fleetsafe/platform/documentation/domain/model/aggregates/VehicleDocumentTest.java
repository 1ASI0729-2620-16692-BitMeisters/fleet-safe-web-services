package com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.UpdateVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentStatus;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.ValidityPeriod;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VehicleDocumentTest {

    private final LocalDate today = LocalDate.of(2026, 10, 28);
    private final VehicleId truck = new VehicleId(UUID.randomUUID());
    private final DocumentTypeId soat = new DocumentTypeId(UUID.randomUUID());

    private VehicleDocument soatExpiringIn(int days) {
        return new VehicleDocument(new RegisterVehicleDocumentCommand(truck, soat, new DocumentNumber("SOAT-2026-004512"),
                new ValidityPeriod(today.minusYears(1), today.plusDays(days)), null), today);
    }

    @Test
    void isValidWhenItExpiresAfterTheAlertWindow() {
        assertThat(soatExpiringIn(VehicleDocument.ALERT_DAYS + 1).getStatus()).isEqualTo(DocumentStatus.VALID);
    }

    @Test
    void isExpiringOnTheLastDayOfTheAlertWindow() {
        assertThat(soatExpiringIn(VehicleDocument.ALERT_DAYS).getStatus()).isEqualTo(DocumentStatus.EXPIRING);
    }

    @Test
    void isStillExpiringOnItsExpirationDay() {
        var document = soatExpiringIn(0);

        assertThat(document.getStatus()).isEqualTo(DocumentStatus.EXPIRING);
        assertThat(document.isExpired(today)).isFalse();
    }

    @Test
    void isExpiredTheDayAfterItsExpirationDate() {
        var document = soatExpiringIn(-1);

        assertThat(document.getStatus()).isEqualTo(DocumentStatus.EXPIRED);
        assertThat(document.isExpired(today)).isTrue();
        assertThat(document.daysUntilExpiration(today)).isEqualTo(-1);
    }

    @Test
    void becomesExpiringAsTimePasses() {
        var document = soatExpiringIn(40);

        assertThat(document.recalculateStatus(today.plusDays(10))).isEqualTo(DocumentStatus.EXPIRING);
    }

    @Test
    void recalculatesItsStatusWhenRenewed() {
        var document = soatExpiringIn(-5);

        document.update(new UpdateVehicleDocumentCommand(UUID.randomUUID(), truck, soat, new DocumentNumber("SOAT-2027-000031"),
                new ValidityPeriod(today, today.plusYears(1)), new FileUrl("https://storage.example.com/soat.pdf")), today);

        assertThat(document.getStatus()).isEqualTo(DocumentStatus.VALID);
        assertThat(document.getNumber().value()).isEqualTo("SOAT-2027-000031");
    }

    @Test
    void belongsOnlyToItsVehicle() {
        var document = soatExpiringIn(10);

        assertThat(document.belongsTo(truck)).isTrue();
        assertThat(document.belongsTo(new VehicleId(UUID.randomUUID()))).isFalse();
    }

    @Test
    void rejectsAnExpirationDateBeforeTheIssueDate() {
        assertThatThrownBy(() -> new ValidityPeriod(today, today.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("documentation.document.error.period.order");
    }

    @Test
    void rejectsALinkThatIsNotHttp() {
        assertThatThrownBy(() -> new FileUrl("ftp://storage.example.com/soat.pdf"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("documentation.document.error.file-url.pattern");
    }

    @Test
    void rejectsABlankNumber() {
        assertThatThrownBy(() -> new DocumentNumber(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("documentation.document.error.number.required");
    }

    @Test
    void normalizesTheDocumentTypeCode() {
        assertThat(new DocumentTypeCode(" soat ").value()).isEqualTo("SOAT");
    }
}
