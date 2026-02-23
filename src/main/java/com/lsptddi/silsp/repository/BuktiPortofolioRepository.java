package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.BuktiPortofolio;
import com.lsptddi.silsp.model.PermohonanSertifikasi;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuktiPortofolioRepository extends JpaRepository<BuktiPortofolio, Long> {
    List<BuktiPortofolio> findByPermohonan(PermohonanSertifikasi permohonan);
}