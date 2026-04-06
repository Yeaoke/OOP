package org.example.repository;

import org.example.model.HoldingCompanies;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface holdingRealization extends CrudRepository<HoldingCompanies, Long> {
    Optional<HoldingCompanies> findByHoldingName(String name);
}