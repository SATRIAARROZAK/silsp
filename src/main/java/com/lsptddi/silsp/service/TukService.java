package com.lsptddi.silsp.service;

import com.lsptddi.silsp.repository.TukRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TukService {

    @Autowired
    private TukRepository tukRepository;

    public String generateTukCode() {
        String lastCode = tukRepository.findLastCode();
        // Format: TUK-LSPTDDI-001
        
        if (lastCode == null) {
            return "TUK-LSPTDDI-001";
        }

        try {
            // Ambil 3 digit terakhir
            String numberPart = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            int number = Integer.parseInt(numberPart);
            number++; // Tambah 1
            
            // Format ulang dengan padding 0 (misal: 1 -> 001)
            return String.format("TUK-LSPTDDI-%03d", number);
        } catch (Exception e) {
            // Fallback jika format di DB rusak
            return "TUK-LSPTDDI-" + System.currentTimeMillis(); 
        }
    }
}