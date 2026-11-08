package com.bitmeisters.fleetsafe.platform.fleet.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.CompanyCommandFailure;
import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.CompanyCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterCompanyCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.CompanyRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private final CompanyRepository companyRepository;

    public CompanyCommandServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Result<Company, CompanyCommandFailure> handle(RegisterCompanyCommand command) {
        if (companyRepository.existsById(command.companyId())) {
            log.warn("Company already registered: companyId={}", command.companyId().value());
            return Result.failure(new CompanyCommandFailure.AlreadyRegistered());
        }
        if (companyRepository.existsByTaxId(command.taxId())) {
            log.warn("Duplicate tax identifier: taxId={}", command.taxId().value());
            return Result.failure(new CompanyCommandFailure.DuplicateTaxId());
        }
        var company = companyRepository.save(new Company(command));
        log.info("Company registered: id={}, taxId={}", company.getId(), command.taxId().value());
        return Result.success(company);
    }
}
