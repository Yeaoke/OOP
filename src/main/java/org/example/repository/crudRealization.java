package org.example.repository;

import org.example.model.IndustrialCompanies;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface crudRealization extends CrudRepository<IndustrialCompanies, Long> {
}