package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.queryservices.InspectionQueryService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionByIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionsQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories.InspectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the InspectionQueryService.
 *
 * <p>
 * Handles read queries for vehicle pre-operational inspections.
 * </p>
 */
@Slf4j
@Service
public class InspectionQueryServiceImpl implements InspectionQueryService {

    private final InspectionRepository inspectionRepository;

    /**
     * Constructs the query service with its required repository dependency.
     *
     * @param inspectionRepository the inspection domain repository
     */
    public InspectionQueryServiceImpl(InspectionRepository inspectionRepository) {
        this.inspectionRepository = inspectionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inspection> handle(GetInspectionByIdQuery query) {
        var result = inspectionRepository.findById(query.inspectionId());
        if (result.isEmpty()) {
            log.debug("Inspection not found: id={}", query.inspectionId().value());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inspection> handle(GetInspectionsQuery query) {
        var results = inspectionRepository.findAll();
        log.debug("Retrieved {} total inspections", results.size());
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inspection> handle(GetInspectionsByVehicleIdQuery query) {
        var results = inspectionRepository.findAllByVehicleIdOrderByStartedAtDesc(query.vehicleId());
        log.debug("Retrieved {} inspections for vehicle: vehicleId={}", results.size(), query.vehicleId().value());
        return results;
    }
}
