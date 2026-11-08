package com.bitmeisters.fleetsafe.platform.incident.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.incident.application.queryservices.IncidentTypeQueryService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentTypeByIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentTypesQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class IncidentTypeQueryServiceImpl implements IncidentTypeQueryService {

    private final IncidentTypeRepository incidentTypeRepository;

    public IncidentTypeQueryServiceImpl(IncidentTypeRepository incidentTypeRepository) {
        this.incidentTypeRepository = incidentTypeRepository;
    }

    @Override
    public List<IncidentType> handle(GetIncidentTypesQuery query) {
        if (query.activeOnly()) {
            return incidentTypeRepository.findAllByActiveTrue();
        }

        return incidentTypeRepository.findAll();
    }

    @Override
    public Optional<IncidentType> handle(GetIncidentTypeByIdQuery query) {
        return incidentTypeRepository.findById(query.incidentTypeId());
    }
}