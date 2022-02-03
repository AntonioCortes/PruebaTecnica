package com.cds.PruebaTecnica.repository;

import com.cds.PruebaTecnica.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RowRepository extends JpaRepository<Row, Integer> {
}
