package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.model.HoldingCompanies;
import org.example.model.IndustrialCompanies;
import org.example.repository.HoldingCompaniesRepository;
import org.example.repository.IndustrialCompaniesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HoldingCompanyService {

    private final HoldingCompaniesRepository holdingRepository;
    private final IndustrialCompaniesRepository companyRepository;

    @Transactional(readOnly = true)
    public List<HoldingCompanies> findAll() {
        return holdingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public HoldingCompanies findByName(String name) {
        return holdingRepository.findByHoldingName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Холдинг \"" + name + "\" не найден"
                        )
                );
    }

    public HoldingCompanies create(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Название холдинга не может быть пустым"
            );
        }

        name = name.trim();

        if (holdingRepository.findByHoldingName(name).isPresent()) {
            throw new IllegalArgumentException(
                    "Холдинг с таким именем уже существует"
            );
        }

        HoldingCompanies holding = new HoldingCompanies();
        holding.setHoldingName(name);

        return holdingRepository.save(holding);
    }

    public void delete(String name) {

        HoldingCompanies holding = findByName(name);

        if (holding.getIndustrialCompanies() != null) {
            for (IndustrialCompanies company :
                    holding.getIndustrialCompanies()) {

                company.setHoldingCompany(null);
                companyRepository.save(company);
            }
        }

        holdingRepository.delete(holding);
    }

    public void assignCompany(
            String holdingName,
            Long companyId
    ) {

        HoldingCompanies holding = findByName(holdingName);

        IndustrialCompanies company =
                companyRepository.findById(companyId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Компания не найдена"
                                )
                        );

        if (company.getHoldingCompany() != null) {
            company.getHoldingCompany()
                    .getIndustrialCompanies()
                    .remove(company);
        }

        holding.getIndustrialCompanies().add(company);
        company.setHoldingCompany(holding);

        companyRepository.save(company);
    }

    public void removeCompany(
            String holdingName,
            Long companyId
    ) {

        HoldingCompanies holding = findByName(holdingName);

        IndustrialCompanies company =
                companyRepository.findById(companyId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Компания не найдена"
                                )
                        );

        holding.getIndustrialCompanies().remove(company);
        company.setHoldingCompany(null);

        companyRepository.save(company);
    }
}