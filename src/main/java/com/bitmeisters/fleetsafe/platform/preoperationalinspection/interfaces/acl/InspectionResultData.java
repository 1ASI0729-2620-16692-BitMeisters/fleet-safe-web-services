package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.acl;

import java.util.UUID;

public record InspectionResultData(UUID resultId, UUID inspectionItemId, String result) {
}
