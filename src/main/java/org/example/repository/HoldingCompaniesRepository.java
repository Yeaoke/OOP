package org.example.repository;

import org.example.model.HoldingCompanies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoldingCompaniesRepository extends JpaRepository<HoldingCompanies, Long> {
    Optional<HoldingCompanies> findByHoldingName(String name);
}