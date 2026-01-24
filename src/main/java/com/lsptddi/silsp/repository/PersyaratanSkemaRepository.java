package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.PersyaratanSkema;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersyaratanSkemaRepository extends JpaRepository<PersyaratanSkema, Long> {
    List<PersyaratanSkema> findBySkemaId(Long skemaId);
}