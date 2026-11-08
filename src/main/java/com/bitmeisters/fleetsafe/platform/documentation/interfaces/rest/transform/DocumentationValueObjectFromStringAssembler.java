package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeName;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.ValidityPeriod;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;

import java.time.LocalDate;
import java.util.UUID;

public class DocumentationValueObjectFromStringAssembler {

    public static VehicleId toVehicleIdFromUuid(UUID value) {
        return new VehicleId(value);
    }

    public static DocumentTypeId toDocumentTypeIdFromUuid(UUID value) {
        return new DocumentTypeId(value);
    }

    public static DocumentTypeCode toDocumentTypeCodeFromString(String value) {
        return new DocumentTypeCode(value);
    }

    public static DocumentTypeName toDocumentTypeNameFromString(String value) {
        return new DocumentTypeName(value);
    }

    public static DocumentNumber toDocumentNumberFromString(String value) {
        return new DocumentNumber(value);
    }

    public static ValidityPeriod toValidityPeriodFromDates(LocalDate issueDate, LocalDate expirationDate) {
        return new ValidityPeriod(issueDate, expirationDate);
    }

    public static FileUrl toFileUrlFromString(String value) {
        return value == null || value.isBlank() ? null : new FileUrl(value);
    }
}
