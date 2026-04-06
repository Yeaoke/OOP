package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "coal_company")
@DiscriminatorValue("CoalCompany")
@PrimaryKeyJoinColumn(name = "industrial_companies_id")
public class CoalCompany extends IndustrialCompanies {

    @Column(name = "coal_volume")
    private Long coalVolume;

    @Column(name = "mine_count")
    private Long mineCount;

    @Column(name = "coal_action")
    private String coalAction;

    public CoalCompany() {}

    @Override
    public void showInfo() {
        System.out.println("=== Угольная компания ===");
        System.out.println("ID: " + getId());
        System.out.println("Название: " + getCompanyName());
        System.out.println("Оборот: " + getAnnualTurnover());
        System.out.println("Объём угля: " + coalVolume);
        System.out.println("Шахт: " + mineCount);
        if (coalAction != null) {
            System.out.println("Действие: " + coalAction);
        }
    }

    @Override
    public void CostOfTimeProduction() {
        Long counter;
        if (coalVolume != null && coalVolume > 0 && getAnnualTurnover() != null) {
            counter = getAnnualTurnover() / coalVolume;
            System.out.println("Стоимость за единицу: " + counter);
            setCoalAction("Стоимость за единицу: " + counter);
        }
    }

    public void addCoalMines(Long count) {
        if (count != null && count > 0) {
            this.mineCount = (this.mineCount == null ? 0 : this.mineCount) + count;
            System.out.println("Добавлено шахт: " + count);
            setCoalAction("Добавлено шахт: " + count);
        }
    }

    public void stopExpanding() {
        this.coalAction = "STOPPED";
        System.out.println("Производство остановлено");
        setCoalAction("Производство остановлено");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}