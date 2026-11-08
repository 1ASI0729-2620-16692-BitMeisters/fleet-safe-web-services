package com.bitmeisters.fleetsafe.platform.documentation.application.queryservices;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetDocumentsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetExpiringDocumentsQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetVehicleDocumentsQuery;

import java.util.List;

public interface VehicleDocumentQueryService {

    List<VehicleDocument> handle(GetVehicleDocumentsQuery query);

    List<VehicleDocument> handle(GetDocumentsByVehicleIdQuery query);

    List<VehicleDocument> handle(GetExpiringDocumentsQuery query);
}
