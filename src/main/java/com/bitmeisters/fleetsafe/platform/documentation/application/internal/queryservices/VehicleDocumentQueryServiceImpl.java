package com.bitmeisters.fleetsafe.platform.documentation.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.documentation.application.internal.outboundservices.calendar.DocumentationCalendar;
import com.bitmeisters.fleetsafe.platform.documentation.application.queryservices.VehicleDocumentQueryService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetDocumentsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetExpiringDocumentsQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetVehicleDocumentsQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.VehicleDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class VehicleDocumentQueryServiceImpl implements VehicleDocumentQueryService {

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final DocumentationCalendar documentationCalendar;

    public VehicleDocumentQueryServiceImpl(VehicleDocumentRepository vehicleDocumentRepository,
                                           DocumentationCalendar documentationCalendar) {
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.documentationCalendar = documentationCalendar;
    }

    private List<VehicleDocument> withCurrentStatus(List<VehicleDocument> vehicleDocuments) {
        var today = documentationCalendar.today();
        vehicleDocuments.forEach(vehicleDocument -> vehicleDocument.recalculateStatus(today));
        return vehicleDocuments.stream()
                .sorted(Comparator.comparing(vehicleDocument -> vehicleDocument.getValidityPeriod().expirationDate()))
                .toList();
    }

    @Override
    public List<VehicleDocument> handle(GetVehicleDocumentsQuery query) {
        var results = withCurrentStatus(vehicleDocumentRepository.findAll());
        log.debug("Found {} vehicle documents", results.size());
        return results;
    }

    @Override
    public List<VehicleDocument> handle(GetDocumentsByVehicleIdQuery query) {
        var results = withCurrentStatus(vehicleDocumentRepository.findAllByVehicleId(query.vehicleId()));
        log.debug("Found {} documents for vehicle {}", results.size(), query.vehicleId().value());
        return results;
    }

    @Override
    public List<VehicleDocument> handle(GetExpiringDocumentsQuery query) {
        var today = documentationCalendar.today();
        var results = withCurrentStatus(vehicleDocumentRepository.findAllByExpirationDateBetween(today, today.plusDays(query.days())));
        log.debug("Found {} documents expiring within {} days", results.size(), query.days());
        return results;
    }
}
