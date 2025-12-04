package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Tuk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TukRepository extends JpaRepository<Tuk, Long> {
    
    // Ambil kode TUK terakhir yang paling besar (DESC limit 1)
    // Format kode: TUK-LSPTDDI-xxx
    @Query(value = "SELECT code FROM tuk ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastCode();
}