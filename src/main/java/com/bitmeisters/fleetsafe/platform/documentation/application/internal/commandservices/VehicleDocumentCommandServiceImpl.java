package com.bitmeisters.fleetsafe.platform.documentation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.VehicleDocumentCommandFailure;
import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.VehicleDocumentCommandService;
import com.bitmeisters.fleetsafe.platform.documentation.application.internal.outboundservices.calendar.DocumentationCalendar;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RecalculateDocumentStatusCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.UpdateVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.DocumentTypeRepository;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.VehicleDocumentRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VehicleDocumentCommandServiceImpl implements VehicleDocumentCommandService {

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentationCalendar documentationCalendar;

    public VehicleDocumentCommandServiceImpl(VehicleDocumentRepository vehicleDocumentRepository,
                                             DocumentTypeRepository documentTypeRepository,
                                             DocumentationCalendar documentationCalendar) {
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentationCalendar = documentationCalendar;
    }

    @Override
    @Transactional
    public Result<VehicleDocument, VehicleDocumentCommandFailure> handle(RegisterVehicleDocumentCommand command) {
        if (!documentTypeRepository.existsById(command.documentTypeId())) {
            log.warn("Unknown document type: documentTypeId={}", command.documentTypeId().value());
            return Result.failure(new VehicleDocumentCommandFailure.UnknownDocumentType());
        }
        var vehicleDocument = vehicleDocumentRepository.save(new VehicleDocument(command, documentationCalendar.today()));
        log.info("Vehicle document registered: id={}, vehicleId={}, documentTypeId={}, status={}",
                vehicleDocument.getId(), command.vehicleId().value(), command.documentTypeId().value(), vehicleDocument.getStatus());
        return Result.success(vehicleDocument);
    }

    @Override
    @Transactional
    public Result<VehicleDocument, VehicleDocumentCommandFailure> handle(UpdateVehicleDocumentCommand command) {
        var vehicleDocument = vehicleDocumentRepository.findById(command.vehicleDocumentId())
                .filter(document -> document.belongsTo(command.vehicleId()));
        if (vehicleDocument.isEmpty()) {
            log.warn("Vehicle document not found: id={}, vehicleId={}", command.vehicleDocumentId(), command.vehicleId().value());
            return Result.failure(new VehicleDocumentCommandFailure.NotFound());
        }
        if (!documentTypeRepository.existsById(command.documentTypeId())) {
            log.warn("Unknown document type: documentTypeId={}", command.documentTypeId().value());
            return Result.failure(new VehicleDocumentCommandFailure.UnknownDocumentType());
        }
        vehicleDocument.get().update(command, documentationCalendar.today());
        var updatedVehicleDocument = vehicleDocumentRepository.save(vehicleDocument.get());
        log.info("Vehicle document updated: id={}, status={}", updatedVehicleDocument.getId(), updatedVehicleDocument.getStatus());
        return Result.success(updatedVehicleDocument);
    }

    @Override
    @Transactional
    public int handle(RecalculateDocumentStatusCommand command) {
        var today = documentationCalendar.today();
        var changed = 0;
        for (var vehicleDocument : vehicleDocumentRepository.findAll()) {
            var previous = vehicleDocument.getStatus();
            if (vehicleDocument.recalculateStatus(today) != previous) {
                vehicleDocumentRepository.save(vehicleDocument);
                changed++;
            }
        }
        log.info("Document status recalculated: today={}, changed={}", today, changed);
        return changed;
    }
}
