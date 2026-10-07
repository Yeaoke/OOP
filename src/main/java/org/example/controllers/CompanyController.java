package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dto.CompanyResponse;
import org.example.dto.CreateCompanyRequest;
import org.example.model.CoalCompany;
import org.example.model.IndustrialCompanies;
import org.example.model.OilCompany;
import org.example.services.CoalCompanyService;
import org.example.services.IndustrialCompaniesService;
import org.example.services.OilCompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final IndustrialCompaniesService companyService;
    private final CoalCompanyService coalCompanyService;
    private final OilCompanyService oilCompanyService;

    @GetMapping
    public List<CompanyResponse> findAll() {
        return companyService.findAll()
                .stream()
                .map(CompanyResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CompanyResponse findById(
            @PathVariable Long id
    ) {
        return CompanyResponse.from(
                companyService.findById(id)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse create(
            @RequestBody CreateCompanyRequest request
    ) {

        IndustrialCompanies company;

        if ("CoalCompany".equals(request.type())) {

            CoalCompany coal = new CoalCompany();

            coal.setCoalVolume(request.coalVolume());
            coal.setMineCount(request.mineCount());

            company = coal;

        } else if ("OilCompany".equals(request.type())) {

            OilCompany oil = new OilCompany();

            oil.setOilVolume(request.oilVolume());
            oil.setHoleCount(request.wellCount());

            company = oil;

        } else {
            throw new IllegalArgumentException(
                    "Неизвестный тип компании"
            );
        }

        company.setCompanyName(request.name());
        company.setAnnualTurnover(request.turnover());

        if (request.holdingName() != null
                && !request.holdingName().isBlank()
                && !"Без холдинга".equals(request.holdingName())) {

            company = companyService.assignHolding(
                    companyService.save(company).getId(),
                    request.holdingName()
            );
        } else {
            company = companyService.save(company);
        }

        return CompanyResponse.from(company);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        companyService.delete(id);
    }

    @PostMapping("/{id}/calculate")
    public CompanyResponse calculate(
            @PathVariable Long id
    ) {
        return CompanyResponse.from(
                companyService.calculate(id)
        );
    }

    @PostMapping("/{id}/holding")
    public CompanyResponse assignHolding(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        return CompanyResponse.from(
                companyService.assignHolding(id, name)
        );
    }

    @DeleteMapping("/{id}/holding")
    public CompanyResponse removeFromHolding(
            @PathVariable Long id
    ) {
        return CompanyResponse.from(
                companyService.removeFromHolding(id)
        );
    }

    @PostMapping("/{id}/mines")
    public CompanyResponse addMines(
            @PathVariable Long id,
            @RequestParam Long count
    ) {
        return CompanyResponse.from(
                coalCompanyService.addMines(id, count)
        );
    }

    @PostMapping("/{id}/stop-expanding")
    public CompanyResponse stopExpanding(
            @PathVariable Long id
    ) {
        return CompanyResponse.from(
                coalCompanyService.stopExpanding(id)
        );
    }

    @PostMapping("/{id}/wells")
    public CompanyResponse addWells(
            @PathVariable Long id,
            @RequestParam Long count
    ) {
        return CompanyResponse.from(
                oilCompanyService.addWells(id, count)
        );
    }

    @PostMapping("/{id}/check-resources")
    public CompanyResponse checkResources(
            @PathVariable Long id
    ) {
        return CompanyResponse.from(
                oilCompanyService.checkResources(id)
        );
    }
}