package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dto.CompanyResponse;
import org.example.services.OilCompanyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oil-companies/{id}")
@RequiredArgsConstructor
public class OilCompanyController {

    private final OilCompanyService oilCompanyService;

    @PostMapping("/wells")
    public CompanyResponse addWells(
            @PathVariable Long id,
            @RequestParam Long count
    ) {
        return CompanyResponse.from(
                oilCompanyService.addWells(id, count)
        );
    }

    @PostMapping("/check-resources")
    public CompanyResponse checkResources(
            @PathVariable Long id
    ) {
        return CompanyResponse.from(
                oilCompanyService.checkResources(id)
        );
    }
}