package com.cds.PruebaTecnica.repository;

import com.cds.PruebaTecnica.entity.ProcessedJSON;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedJSONRepository extends JpaRepository<ProcessedJSON, Integer>
{
}
