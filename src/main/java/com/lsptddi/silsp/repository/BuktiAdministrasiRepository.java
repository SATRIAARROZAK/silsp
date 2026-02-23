package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.BuktiAdministrasi;
import com.lsptddi.silsp.model.PermohonanSertifikasi;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BuktiAdministrasiRepository extends JpaRepository<BuktiAdministrasi, Long> {
    BuktiAdministrasi findByPermohonanAndJenisBukti(PermohonanSertifikasi permohonan, String jenisBukti);
}