package com.lsptddi.silsp.service;

import com.lsptddi.silsp.dto.ScheduleDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private TukRepository tukRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkemaRepository schemaRepository;
    @Autowired
    private TypeSumberAnggaranRepository typeSumberAnggaranRepository;
    @Autowired
    private TypePemberiAnggaranRepository typePemberiAnggaranRepository;

    // Generate Format: Jadwal-LSPTDDI-001-2025
    public String generateScheduleCode() {
        Optional<Schedule> lastSchedule = scheduleRepository.findTopByOrderByIdDesc();
        int nextId = 1;
        int currentYear = Year.now().getValue();

        if (lastSchedule.isPresent()) {
            String lastCode = lastSchedule.get().getCode();
            // Cek apakah kode tahunnya sama
            if (lastCode != null && lastCode.endsWith("-" + currentYear)) {
                try {
                    // Jadwal-LSPTDDI-XXX-YYYY
                    String[] parts = lastCode.split("-");
                    if (parts.length >= 3) {
                        nextId = Integer.parseInt(parts[2]) + 1;
                    }
                } catch (Exception e) {
                    nextId = 1;
                }
            }
        }
        return String.format("Jadwal-LSPTDDI-%03d-%d", nextId, currentYear);
    }

    @Transactional
    public void saveSchedule(ScheduleDto dto) {
        Schedule schedule = new Schedule();
        schedule.setName(dto.getName());
        schedule.setCode(generateScheduleCode()); // Auto Generate
        schedule.setBnspCode(dto.getBnspCode());
        schedule.setStartDate(dto.getStartDate());
        schedule.setQuota(dto.getQuota());
        TypeSumberAnggaran budgetSource = typeSumberAnggaranRepository.findById(dto.getBudgetSource())
                .orElseThrow(() -> new RuntimeException("Budget source not found"));
        schedule.setBudgetSource(budgetSource);
        TypePemberiAnggaran budgetProvider = typePemberiAnggaranRepository.findById(dto.getBudgetProvider())
                .orElseThrow(() -> new RuntimeException("Budget provider not found"));
        schedule.setBudgetProvider(budgetProvider);

        // Set TUK
        Tuk tuk = tukRepository.findById(dto.getTukId())
                .orElseThrow(() -> new RuntimeException("TUK not found"));
        schedule.setTuk(tuk);

        // Save Parent dulu untuk dapat ID
        schedule = scheduleRepository.save(schedule);

        // Set Asesors
        if (dto.getAssessorIds() != null) {
            List<ScheduleAssessor> assessorList = new ArrayList<>();
            for (Long userId : dto.getAssessorIds()) {
                User user = userRepository.findById(userId).orElseThrow();
                ScheduleAssessor sa = new ScheduleAssessor();
                sa.setSchedule(schedule);
                sa.setAsesor(user);
                assessorList.add(sa);
            }
            schedule.setAssessors(assessorList);
        }

        // Set Schemas
        if (dto.getSchemaIds() != null) {
            List<ScheduleSchema> schemaList = new ArrayList<>();
            for (Long schemaId : dto.getSchemaIds()) {
                com.lsptddi.silsp.model.Skema sc = schemaRepository.findById(schemaId).orElseThrow();
                ScheduleSchema ss = new ScheduleSchema();
                ss.setSchedule(schedule);
                ss.setSchema(sc);
                schemaList.add(ss);
            }
            schedule.setSchemas(schemaList);
        }

        // Save lagi dengan relasi
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void updateSchedule(ScheduleDto dto) {
        // 1. Ambil Data Lama (Existing)
        Schedule schedule = scheduleRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Jadwal dengan ID " + dto.getId() + " tidak ditemukan."));

        // 2. Update Field Dasar
        schedule.setName(dto.getName());
        schedule.setBnspCode(dto.getBnspCode());
        schedule.setStartDate(dto.getStartDate());
        schedule.setQuota(dto.getQuota());

        // 3. Update Relasi TUK & Anggaran (Jika Berubah)
        if (!schedule.getTuk().getId().equals(dto.getTukId())) {
            Tuk newTuk = tukRepository.findById(dto.getTukId())
                    .orElseThrow(() -> new RuntimeException("TUK tidak ditemukan"));
            schedule.setTuk(newTuk);
        }

        // Update Anggaran (Handle null safety)
        if (dto.getBudgetSource() != null) {
            TypeSumberAnggaran src = typeSumberAnggaranRepository.findById(dto.getBudgetSource()).orElse(null);
            schedule.setBudgetSource(src);
        }
        if (dto.getBudgetProvider() != null) {
            TypePemberiAnggaran prov = typePemberiAnggaranRepository.findById(dto.getBudgetProvider()).orElse(null);
            schedule.setBudgetProvider(prov);
        }

        // 4. UPDATE RELASI LIST (PENTING: CLEAR & RE-ADD)
        // Spring Data JPA dengan orphanRemoval=true akan otomatis menghapus data lama
        // di DB

        // A. Asesor
        if (dto.getAssessorIds() != null) {
            schedule.getAssessors().clear(); // Hapus yang lama
            for (Long userId : dto.getAssessorIds()) {
                User user = userRepository.findById(userId).orElseThrow();
                ScheduleAssessor sa = new ScheduleAssessor();
                sa.setSchedule(schedule);
                sa.setAsesor(user);
                schedule.getAssessors().add(sa); // Tambah yang baru
            }
        }

        // B. Skema
        if (dto.getSchemaIds() != null) {
            schedule.getSchemas().clear(); // Hapus yang lama
            for (Long schemaId : dto.getSchemaIds()) {
                com.lsptddi.silsp.model.Skema sc = schemaRepository.findById(schemaId).orElseThrow();
                ScheduleSchema ss = new ScheduleSchema();
                ss.setSchedule(schedule);
                ss.setSchema(sc);
                schedule.getSchemas().add(ss); // Tambah yang baru
            }
        }

        // 5. Simpan Perubahan
        scheduleRepository.save(schedule);
    }
}