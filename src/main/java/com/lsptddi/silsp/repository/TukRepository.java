package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Tuk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface TukRepository extends JpaRepository<Tuk, Long> {

    // Ambil kode TUK terakhir yang paling besar (DESC limit 1)
    // Format kode: TUK-LSPTDDI-xxx
    // @Query(value = "SELECT code FROM tuk ORDER BY id DESC LIMIT 1", nativeQuery = true)
    // String findLastCode();
    Optional<Tuk> findTopByOrderByIdDesc();

    // Query Search: Cari berdasarkan Nama TUK atau Kode TUK
    @Query("SELECT t FROM Tuk t WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Tuk> searchTuk(@Param("keyword") String keyword, Pageable pageable);

}