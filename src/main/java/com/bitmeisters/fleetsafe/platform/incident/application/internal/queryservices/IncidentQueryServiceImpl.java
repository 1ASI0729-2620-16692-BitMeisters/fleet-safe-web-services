package com.bitmeisters.fleetsafe.platform.incident.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.incident.application.queryservices.IncidentQueryService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetCorrectiveActionsByIncidentIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentByIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentFollowUpsQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsByStatusQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetRepairsByIncidentIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.CorrectiveActionRepository;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentFollowUpRepository;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentRepository;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.RepairRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class IncidentQueryServiceImpl implements IncidentQueryService {

    private final IncidentRepository incidentRepository;
    private final CorrectiveActionRepository correctiveActionRepository;
    private final RepairRepository repairRepository;
    private final IncidentFollowUpRepository incidentFollowUpRepository;

    public IncidentQueryServiceImpl(
            IncidentRepository incidentRepository,
            CorrectiveActionRepository correctiveActionRepository,
            RepairRepository repairRepository,
            IncidentFollowUpRepository incidentFollowUpRepository) {
        this.incidentRepository = incidentRepository;
        this.correctiveActionRepository = correctiveActionRepository;
        this.repairRepository = repairRepository;
        this.incidentFollowUpRepository = incidentFollowUpRepository;
    }

    @Override
    public List<Incident> handle(GetIncidentsQuery query) {
        return incidentRepository.findAll();
    }

    @Override
    public Optional<Incident> handle(GetIncidentByIdQuery query) {
        return incidentRepository.findById(query.incidentId());
    }

    @Override
    public List<Incident> handle(GetIncidentsByVehicleIdQuery query) {
        return incidentRepository.findAllByVehicleId(query.vehicleId());
    }

    @Override
    public List<Incident> handle(GetIncidentsByStatusQuery query) {
        return incidentRepository.findAllByStatus(query.status());
    }

    @Override
    public List<CorrectiveAction> handle(GetCorrectiveActionsByIncidentIdQuery query) {
        return correctiveActionRepository.findAllByIncidentId(query.incidentId());
    }

    @Override
    public List<Repair> handle(GetRepairsByIncidentIdQuery query) {
        return repairRepository.findAllByIncidentId(query.incidentId());
    }

    @Override
    public List<IncidentFollowUp> handle(GetIncidentFollowUpsQuery query) {
        return incidentFollowUpRepository
                .findAllByIncidentIdOrderByCreatedAtDesc(query.incidentId());
    }
}