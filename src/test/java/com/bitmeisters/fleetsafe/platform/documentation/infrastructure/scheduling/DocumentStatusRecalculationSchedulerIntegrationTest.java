package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.scheduling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.CronTask;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DocumentStatusRecalculationSchedulerIntegrationTest {

    @Autowired
    private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

    @Test
    void recalculatesTheDocumentStatusEveryDayAtFive() {
        assertThat(scheduledAnnotationBeanPostProcessor.getScheduledTasks())
                .map(scheduledTask -> scheduledTask.getTask())
                .filteredOn(task -> task.toString().contains("recalculateDocumentStatus"))
                .singleElement()
                .isInstanceOfSatisfying(CronTask.class, task -> assertThat(task.getExpression()).isEqualTo("0 0 5 * * *"));
    }
}
