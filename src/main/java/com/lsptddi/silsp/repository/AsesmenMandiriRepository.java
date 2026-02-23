package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.AsesmenMandiri;
import com.lsptddi.silsp.model.PermohonanSertifikasi;
import com.lsptddi.silsp.model.UnitElemenSkema;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsesmenMandiriRepository extends JpaRepository<AsesmenMandiri, Long> {
    AsesmenMandiri findByPermohonanAndUnitElemen(PermohonanSertifikasi permohonan, UnitElemenSkema unitElemen);
}