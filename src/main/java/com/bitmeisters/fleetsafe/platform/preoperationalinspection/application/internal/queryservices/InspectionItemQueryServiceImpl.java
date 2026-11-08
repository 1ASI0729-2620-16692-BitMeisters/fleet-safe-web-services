package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.queryservices.InspectionItemQueryService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionItemByIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionItemsQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories.InspectionItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the InspectionItemQueryService.
 *
 * <p>
 * Handles read operations for the inspection items catalog.
 * </p>
 */
@Slf4j
@Service
public class InspectionItemQueryServiceImpl implements InspectionItemQueryService {

    private final InspectionItemRepository inspectionItemRepository;

    /**
     * Constructs the query service with its required repository dependency.
     *
     * @param inspectionItemRepository the domain repository
     */
    public InspectionItemQueryServiceImpl(InspectionItemRepository inspectionItemRepository) {
        this.inspectionItemRepository = inspectionItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspectionItem> handle(GetInspectionItemsQuery query) {
        var results = inspectionItemRepository.findAllByFilters(query.system(), query.active());
        log.debug("Found {} inspection items matching criteria: system={}, active={}",
                results.size(), query.system(), query.active());
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InspectionItem> handle(GetInspectionItemByIdQuery query) {
        var result = inspectionItemRepository.findById(query.id());
        if (result.isEmpty()) {
            log.debug("No inspection item found with id: {}", query.id().value());
        }
        return result;
    }
}
