package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@Table(name = "industrial_companies")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class IndustrialCompanies {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", sequenceName = "company_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    protected String companyName;

    @Column(name = "turnover")
    protected Long annualTurnover;

    @Column(name = "type", insertable = false, updatable = false, length = 50)
    protected String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "holding_companies_id")
    private HoldingCompanies holdingCompany;

    public IndustrialCompanies() {}

    public abstract void CostOfTimeProduction();
    public abstract void showInfo();

    @Override
    public String toString() {
        return id + " " + companyName + " " + type;
    }
}