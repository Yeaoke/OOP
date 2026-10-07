package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dto.CompanyResponse;
import org.example.dto.CreateHoldingRequest;
import org.example.dto.HoldingResponse;
import org.example.model.HoldingCompanies;
import org.example.services.HoldingCompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holdings")
@RequiredArgsConstructor
public class HoldingCompanyController {

    private final HoldingCompanyService holdingService;

    @GetMapping
    public List<HoldingResponse> findAll() {

        return holdingService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{name}")
    public HoldingResponse findByName(
            @PathVariable String name
    ) {
        return toResponse(
                holdingService.findByName(name)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HoldingResponse create(
            @RequestBody CreateHoldingRequest request
    ) {
        return toResponse(
                holdingService.create(request.name())
        );
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String name
    ) {
        holdingService.delete(name);
    }

    @PostMapping("/{name}/companies/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignCompany(
            @PathVariable String name,
            @PathVariable Long companyId
    ) {
        holdingService.assignCompany(name, companyId);
    }

    @DeleteMapping("/{name}/companies/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompany(
            @PathVariable String name,
            @PathVariable Long companyId
    ) {
        holdingService.removeCompany(name, companyId);
    }

    private HoldingResponse toResponse(
            HoldingCompanies holding
    ) {

        List<CompanyResponse> companies =
                holding.getIndustrialCompanies() == null
                        ? List.of()
                        : holding.getIndustrialCompanies()
                        .stream()
                        .map(CompanyResponse::from)
                        .toList();

        return new HoldingResponse(
                holding.getId(),
                holding.getHoldingName(),
                companies
        );
    }
}