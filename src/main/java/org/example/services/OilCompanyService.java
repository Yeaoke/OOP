package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.model.IndustrialCompanies;
import org.example.model.OilCompany;
import org.example.repository.IndustrialCompaniesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OilCompanyService {

    private final IndustrialCompaniesRepository companyRepository;

    public OilCompany findById(Long id) {
        IndustrialCompanies company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Компания с ID " + id + " не найдена"
                        )
                );

        if (!(company instanceof OilCompany oilCompany)) {
            throw new IllegalArgumentException(
                    "Компания с ID " + id + " не является нефтяной"
            );
        }

        return oilCompany;
    }

    public OilCompany addWells(Long id, Long count) {
        if (count == null || count <= 0) {
            throw new IllegalArgumentException(
                    "Количество скважин должно быть больше нуля"
            );
        }

        OilCompany company = findById(id);

        company.addOilWells(count);

        return companyRepository.save(company);
    }

    public OilCompany checkResources(Long id) {
        OilCompany company = findById(id);

        company.checkResources();

        return companyRepository.save(company);
    }
}