package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "oil_company")
@DiscriminatorValue("OilCompany")
@PrimaryKeyJoinColumn(name = "industrial_companies_id")
public class OilCompany extends IndustrialCompanies {

    @Column(name = "oil_volume")
    private Long oilVolume;

    @Column(name = "well_count")
    private Long holeCount;

    @Column(name = "oil_action")
    private String oilAction;

    public OilCompany() {}

    @Override
    public void showInfo() {
        System.out.println("Нефтяная компания");
        System.out.println("ID: " + getId());
        System.out.println("Название: " + getCompanyName());
        System.out.println("Оборот: " + getAnnualTurnover());
        System.out.println("Объём нефти: " + oilVolume);
        System.out.println("Скважин: " + holeCount);
        if (oilAction != null) {
            System.out.println("Действие: " + oilAction);
        }
    }

    @Override
    public void CostOfTimeProduction() {
        if (oilVolume != null && oilVolume > 0 && getAnnualTurnover() != null) {
            System.out.println("Стоимость за единицу: " + (getAnnualTurnover() / oilVolume));
            setOilAction("Стоимость за единицу: " + (getAnnualTurnover() / oilVolume));
        }
    }

    public void addOilWells(Long count) {
        if (count != null && count > 0) {
            this.holeCount = (this.holeCount == null ? 0 : this.holeCount) + count;
            System.out.println("Добавлено скважин: " + count);
            setOilAction("Добавлено скважин: " + count);
        }
    }

    public void checkResources() {
        this.oilAction = "CHECKED";
        System.out.println("Ресурсы проверены");
        setOilAction("Ресурсы проверены");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}