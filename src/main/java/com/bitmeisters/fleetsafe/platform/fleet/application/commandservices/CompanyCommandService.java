package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterCompanyCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface CompanyCommandService {

    Result<Company, CompanyCommandFailure> handle(RegisterCompanyCommand command);
}
