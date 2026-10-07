package org.example.dto;

import org.example.model.CoalCompany;
import org.example.model.IndustrialCompanies;
import org.example.model.OilCompany;

public record CompanyResponse(
        Long id,
        String name,
        String type,
        Long turnover,
        String holdingName,
        String details
) {

    public static CompanyResponse from(
            IndustrialCompanies company
    ) {

        String details = "";

        if (company instanceof CoalCompany coal) {
            details =
                    "Угля: " + coal.getCoalVolume()
                    + ", Шахт: " + coal.getMineCount();

            if (coal.getCoalAction() != null) {
                details += " | " + coal.getCoalAction();
            }

        } else if (company instanceof OilCompany oil) {
            details =
                    "Нефти: " + oil.getOilVolume()
                    + ", Скважин: " + oil.getHoleCount();

            if (oil.getOilAction() != null) {
                details += " | " + oil.getOilAction();
            }
        }

        String holdingName =
                company.getHoldingCompany() != null
                        ? company.getHoldingCompany().getHoldingName()
                        : "—";

        return new CompanyResponse(
                company.getId(),
                company.getCompanyName(),
                company.getType(),
                company.getAnnualTurnover(),
                holdingName,
                details
        );
    }
}