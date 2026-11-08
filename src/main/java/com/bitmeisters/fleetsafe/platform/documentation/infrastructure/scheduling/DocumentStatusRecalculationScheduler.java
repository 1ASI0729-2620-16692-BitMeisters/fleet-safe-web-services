package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.scheduling;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.VehicleDocumentCommandService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RecalculateDocumentStatusCommand;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class DocumentStatusRecalculationScheduler {

    private final VehicleDocumentCommandService vehicleDocumentCommandService;

    public DocumentStatusRecalculationScheduler(VehicleDocumentCommandService vehicleDocumentCommandService) {
        this.vehicleDocumentCommandService = vehicleDocumentCommandService;
    }

    @Scheduled(cron = "${vehicle-documentation.status-recalculation.cron:0 0 5 * * *}",
            zone = "${vehicle-documentation.time-zone:America/Lima}")
    public void recalculateDocumentStatus() {
        vehicleDocumentCommandService.handle(new RecalculateDocumentStatusCommand());
    }
}
