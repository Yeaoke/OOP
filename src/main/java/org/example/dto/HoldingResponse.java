package org.example.dto;

import java.util.List;

public record HoldingResponse(
        Long id,
        String name,
        List<CompanyResponse> companies
) {}