package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemaRepository extends JpaRepository<Schema, Long> {

    // Query Pencarian (Search by Name or Code)
    @Query("SELECT s FROM Schema s WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Schema> searchSchema(@Param("keyword") String keyword, Pageable pageable);
}