package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InspectionItemTest {

    private InspectionItem brakeItem() {
        return new InspectionItem(
                new ItemCode("BRK-01"),
                new ItemName("Service and parking brake functionality"),
                "Verify pedal travel and handbrake response.",
                ItemCategory.SAFETY_COMPONENT,
                ItemSystem.BRAKES,
                true,
                true,
                1);
    }

    @Test
    void isActiveWhenCreated() {
        var item = brakeItem();
        assertThat(item.isActive()).isTrue();
        assertThat(item.getCode().value()).isEqualTo("BRK-01");
        assertThat(item.isSafetyComponent()).isTrue();
        assertThat(item.isRequiresEvidence()).isTrue();
        assertThat(item.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    void updatesSpecifications() {
        var item = brakeItem();

        item.update(
                new ItemName("Updated brake inspection"),
                "Check hydraulic fluid lines and caliper wear.",
                ItemCategory.COMPONENT,
                ItemSystem.BRAKES,
                false,
                false,
                5);

        assertThat(item.getName().value()).isEqualTo("Updated brake inspection");
        assertThat(item.getDescription()).isEqualTo("Check hydraulic fluid lines and caliper wear.");
        assertThat(item.getCategory()).isEqualTo(ItemCategory.COMPONENT);
        assertThat(item.isSafetyComponent()).isFalse();
        assertThat(item.isRequiresEvidence()).isFalse();
        assertThat(item.getDisplayOrder()).isEqualTo(5);
    }

    @Test
    void canBeDeactivatedAndReactivated() {
        var item = brakeItem();

        item.deactivate();
        assertThat(item.isActive()).isFalse();

        item.activate();
        assertThat(item.isActive()).isTrue();
    }

    @Test
    void keepsStateWhenRestored() {
        var id = new InspectionItemId(UUID.randomUUID());
        var created = Instant.parse("2026-11-01T10:00:00Z");
        var updated = Instant.parse("2026-11-02T12:00:00Z");

        var item = InspectionItem.restore(
                id,
                new ItemCode("SAF-01"),
                new ItemName("Fire extinguisher"),
                "Check pressure gauge and expiration date.",
                ItemCategory.SAFETY_COMPONENT,
                ItemSystem.SAFETY_EQUIPMENT,
                true,
                true,
                2,
                false,
                created,
                updated);

        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getCode().value()).isEqualTo("SAF-01");
        assertThat(item.isActive()).isFalse();
        assertThat(item.getCreatedAt()).isEqualTo(created);
        assertThat(item.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    void rejectsMissingNameOnUpdate() {
        var item = brakeItem();

        assertThatThrownBy(() -> item.update(
                null,
                "Desc",
                ItemCategory.COMPONENT,
                ItemSystem.BRAKES,
                false,
                false,
                1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("inspection.item.error.name.required");
    }
}
