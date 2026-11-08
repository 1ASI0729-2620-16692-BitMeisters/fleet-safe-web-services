package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;

import java.util.UUID;

public class EvaluationValueObjectFromStringAssembler {

    public static InspectionItemId toInspectionItemIdFromUuid(UUID value) {
        return new InspectionItemId(value);
    }

    public static RuleName toRuleNameFromString(String value) {
        return new RuleName(value);
    }

    public static ResultValue toResultValueFromString(String value) {
        return ResultValue.valueOf(value);
    }

    public static RuleImpact toRuleImpactFromString(String value) {
        return RuleImpact.valueOf(value);
    }
}
