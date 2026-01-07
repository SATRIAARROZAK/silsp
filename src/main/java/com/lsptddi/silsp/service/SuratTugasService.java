package com.lsptddi.silsp.service;

import com.lsptddi.silsp.repository.SuratTugasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SuratTugasService {

    @Autowired
    private SuratTugasRepository suratRepository;

    public String getRomanMonth(int month) {
        String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        return romanMonths[month - 1];
    }

    public String generateNomorSurat() {
        LocalDate now = LocalDate.now();
        String romanMonth = getRomanMonth(now.getMonthValue());
        int year = now.getYear();

        // Hitung jumlah surat di bulan ini untuk menentukan urutan
        Long count = suratRepository.countByBulanAndTahun(romanMonth, year);
        Long nextSequence = count + 1;

        // Format: 001/ST/LSPTDDI/I/2026
        return String.format("%03d/ST/LSPTDDI/%s/%d", nextSequence, romanMonth, year);
    }
}