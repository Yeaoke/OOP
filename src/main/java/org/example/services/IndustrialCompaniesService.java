package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.model.IndustrialCompanies;
import org.example.model.HoldingCompanies;
import org.example.repository.HoldingCompaniesRepository;
import org.example.repository.IndustrialCompaniesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IndustrialCompaniesService {

    private final IndustrialCompaniesRepository companyRepository;
    private final HoldingCompaniesRepository holdingRepository;

    @Transactional(readOnly = true)
    public List<IndustrialCompanies> findAll() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public IndustrialCompanies findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Компания с ID " + id + " не найдена"
                        )
                );
    }

    public IndustrialCompanies save(IndustrialCompanies company) {
        return companyRepository.save(company);
    }

    public void delete(Long id) {
        IndustrialCompanies company = findById(id);

        if (company.getHoldingCompany() != null) {
            HoldingCompanies holding = company.getHoldingCompany();
            holding.getIndustrialCompanies().remove(company);
            company.setHoldingCompany(null);
        }

        companyRepository.delete(company);
    }

    public IndustrialCompanies calculate(Long id) {
        IndustrialCompanies company = findById(id);

        company.CostOfTimeProduction();

        return companyRepository.save(company);
    }

    public IndustrialCompanies assignHolding(Long companyId, String holdingName) {
        IndustrialCompanies company = findById(companyId);

        HoldingCompanies holding = holdingRepository
                .findByHoldingName(holdingName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Холдинг \"" + holdingName + "\" не найден"
                        )
                );

        if (company.getHoldingCompany() != null) {
            company.getHoldingCompany()
                    .getIndustrialCompanies()
                    .remove(company);
        }

        holding.getIndustrialCompanies().add(company);
        company.setHoldingCompany(holding);

        return companyRepository.save(company);
    }

    public IndustrialCompanies removeFromHolding(Long companyId) {
        IndustrialCompanies company = findById(companyId);

        if (company.getHoldingCompany() == null) {
            throw new IllegalArgumentException(
                    "Компания не находится в холдинге"
            );
        }

        HoldingCompanies holding = company.getHoldingCompany();

        holding.getIndustrialCompanies().remove(company);
        company.setHoldingCompany(null);

        return companyRepository.save(company);
    }
}