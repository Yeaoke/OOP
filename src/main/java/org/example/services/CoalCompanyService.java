package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.model.CoalCompany;
import org.example.model.IndustrialCompanies;
import org.example.repository.IndustrialCompaniesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CoalCompanyService {

    private final IndustrialCompaniesRepository companyRepository;

    public CoalCompany findById(Long id) {
        IndustrialCompanies company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Компания с ID " + id + " не найдена"
                        )
                );

        if (!(company instanceof CoalCompany coalCompany)) {
            throw new IllegalArgumentException(
                    "Компания с ID " + id + " не является угольной"
            );
        }

        return coalCompany;
    }

    public CoalCompany addMines(Long id, Long count) {
        if (count == null || count <= 0) {
            throw new IllegalArgumentException(
                    "Количество шахт должно быть больше нуля"
            );
        }

        CoalCompany company = findById(id);

        company.addCoalMines(count);

        return companyRepository.save(company);
    }

    public CoalCompany stopExpanding(Long id) {
        CoalCompany company = findById(id);

        company.stopExpanding();

        return companyRepository.save(company);
    }
}