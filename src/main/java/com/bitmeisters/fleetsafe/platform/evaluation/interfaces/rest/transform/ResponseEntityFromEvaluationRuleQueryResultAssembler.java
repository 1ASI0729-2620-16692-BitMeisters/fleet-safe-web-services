package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromEvaluationRuleQueryResultAssembler {

    private static ResponseEntity<ProblemDetail> problemDetail(
            HttpStatus status,
            MessageSource messageSource,
            String messageKey, Object... args) {
        var detail = messageSource.getMessage(messageKey, args, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    public static ResponseEntity<?> toResponseEntityFromEvaluationRule(EvaluationRule evaluationRule) {
        return ResponseEntity.ok(EvaluationRuleResourceFromEntityAssembler.toResourceFromEntity(evaluationRule));
    }

    public static ResponseEntity<?> toResponseEntityFromList(List<EvaluationRule> evaluationRules) {
        var evaluationRuleResources = evaluationRules.stream()
                .map(EvaluationRuleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(evaluationRuleResources);
    }

    public static ResponseEntity<ProblemDetail> notFound(MessageSource messageSource, String messageKey, Object... args) {
        return problemDetail(HttpStatus.NOT_FOUND, messageSource, messageKey, args);
    }
}
