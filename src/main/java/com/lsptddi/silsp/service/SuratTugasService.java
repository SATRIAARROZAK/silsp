// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.repository.SuratTugasRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.time.LocalDate;

// @Service
// public class SuratTugasService {

//     @Autowired
//     private SuratTugasRepository suratRepository;

//     public String getRomanMonth(int month) {
//         String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
//         return romanMonths[month - 1];
//     }

//     public String generateNomorSurat() {
//         LocalDate now = LocalDate.now();
//         String romanMonth = getRomanMonth(now.getMonthValue());
//         int year = now.getYear();

//         // Hitung jumlah surat di bulan ini untuk menentukan urutan
//         Long count = suratRepository.countByBulanAndTahun(romanMonth, year);
//         Long nextSequence = count + 1;

//         // Format: 001/ST/LSPTDDI/I/2026
//         return String.format("%03d/ST/LSPTDDI/%s/%d", nextSequence, romanMonth, year);
//     }
// }

package com.lsptddi.silsp.service;

import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SuratTugasService {

    @Autowired private SuratTugasRepository suratRepository;

    // Helper Konversi Bulan ke Romawi
    public String getRomawiMonth(int month) {
        String[] romawi = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        return romawi[month - 1];
    }

    // Logic Generate Nomor Otomatis: 001/ST/LSPTDDI/I/2026
    public String generateNomorSurat() {
        LocalDate now = LocalDate.now();
        String blnRomawi = getRomawiMonth(now.getMonthValue());
        int tahun = now.getYear();

        // Hitung jumlah surat bulan ini + 1
        Long count = suratRepository.countByBulanAndTahun(blnRomawi, tahun);
        long nextNo = count + 1;

        // Format 3 digit (001)
        return String.format("%03d/ST/LSPTDDI/%s/%d", nextNo, blnRomawi, tahun);
    }

    @Transactional
    public SuratTugas createSuratTugas(Schedule jadwal, User asesor, Skema skema) {
        // Cek Duplikat
        if (suratRepository.existsByJadwalAndAsesor(jadwal, asesor)) {
            throw new RuntimeException("Asesor " + asesor.getFullName() + " sudah dibuatkan surat tugas untuk jadwal ini!");
        }

        SuratTugas st = new SuratTugas();
        st.setNomorSurat(generateNomorSurat()); // Auto Generate
        st.setTanggalSurat(LocalDate.now());    // Auto Date Hari Ini
        st.setBulanRomawi(getRomawiMonth(LocalDate.now().getMonthValue()));
        st.setTahun(LocalDate.now().getYear());
        
        st.setJadwal(jadwal);
        st.setAsesor(asesor);
        st.setSkema(skema);

        return suratRepository.save(st);
    }
}