package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dto.CompanyResponse;
import org.example.services.CoalCompanyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coal-companies/{id}")
@RequiredArgsConstructor
public class CoalCompanyController {

    private final CoalCompanyService coalCompanyService;

    @PostMapping("/mines")
    public CompanyResponse addMines(
            @PathVariable Long id,
            @RequestParam Long count
    ) {
        return CompanyResponse.from(
                coalCompanyService.addMines(id, count)
        );
    }

    @PostMapping("/stop-expanding")
    public CompanyResponse stopExpanding(
            @PathVariable Long id
    ) {
        return CompanyResponse.from(
                coalCompanyService.stopExpanding(id)
        );
    }
}