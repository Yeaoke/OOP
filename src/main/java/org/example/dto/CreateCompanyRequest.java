package org.example.dto;

public record CreateCompanyRequest(
        String name,
        Long turnover,
        String type,
        Long coalVolume,
        Long mineCount,
        Long oilVolume,
        Long wellCount,
        String holdingName
) {}