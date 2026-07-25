package org.example.repository;

import org.example.model.IndustrialCompanies;
import org.springframework.data.repository.CrudRepository;


public interface crudRealization extends CrudRepository<IndustrialCompanies, Long> {
}