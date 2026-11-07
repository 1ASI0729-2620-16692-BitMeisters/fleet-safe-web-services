package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyName;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetName;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LoadCapacity;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.ManufacturingYear;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.TaxId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleBrand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleModel;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleType;

import java.math.BigDecimal;
import java.util.UUID;

public class FleetValueObjectFromStringAssembler {

    public static CompanyId toCompanyIdFromUuid(UUID value) {
        return new CompanyId(value);
    }

    public static FleetId toFleetIdFromUuid(UUID value) {
        return new FleetId(value);
    }

    public static VehicleId toVehicleIdFromUuid(UUID value) {
        return new VehicleId(value);
    }

    public static CompanyName toCompanyNameFromString(String value) {
        return new CompanyName(value);
    }

    public static TaxId toTaxIdFromString(String value) {
        return new TaxId(value);
    }

    public static FleetName toFleetNameFromString(String value) {
        return new FleetName(value);
    }

    public static Plate toPlateFromString(String value) {
        return new Plate(value);
    }

    public static VehicleBrand toVehicleBrandFromString(String value) {
        return new VehicleBrand(value);
    }

    public static VehicleModel toVehicleModelFromString(String value) {
        return new VehicleModel(value);
    }

    public static ManufacturingYear toManufacturingYearFromInteger(Integer value) {
        if (value == null) throw new IllegalArgumentException("fleet.vehicle.error.year.required");
        return new ManufacturingYear(value);
    }

    public static LoadCapacity toLoadCapacityFromDecimal(BigDecimal value) {
        return new LoadCapacity(value);
    }

    public static VehicleType toVehicleTypeFromString(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("fleet.vehicle.error.type.required");
        try {
            return VehicleType.valueOf(value.strip().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("fleet.vehicle.error.type.pattern");
        }
    }

    /**
     * The status used to filter the vehicles. Nothing given means no filter.
     */
    public static VehicleStatus toVehicleStatusFilterFromString(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return VehicleStatus.valueOf(value.strip().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("fleet.vehicle.error.status.pattern");
        }
    }
}
