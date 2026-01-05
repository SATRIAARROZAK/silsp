package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.model.Skema;
import com.lsptddi.silsp.repository.SkemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired private SkemaRepository skemaRepository;
    @Autowired private RestTemplate restTemplate;

    // // --- 1. PROXY API BNSP (ANGGARAN) ---
    // @GetMapping("/proxy/jenis-anggaran")
    // public ResponseEntity<?> getJenisAnggaran() {
    //     String url = "https://konstruksi.bnsp.go.id/api/v1/master/jenis-anggaran";
    //     try {
    //         // Ambil data mentah dari BNSP dan teruskan ke Frontend
    //         Object response = restTemplate.getForObject(url, Object.class);
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         return ResponseEntity.internalServerError().body("Gagal mengambil data BNSP");
    //     }
    // }

    // // --- 2. PROXY API BNSP (KEMENTRIAN) ---
    // @GetMapping("/proxy/kementrian")
    // public ResponseEntity<?> getKementrian() {
    //     String url = "https://konstruksi.bnsp.go.id/api/v1/master/kementrian";
    //     try {
    //         Object response = restTemplate.getForObject(url, Object.class);
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         return ResponseEntity.internalServerError().body("Gagal mengambil data BNSP");
    //     }
    // }

    // --- 3. INTERNAL API (UNIT SKEMA) ---
    @GetMapping("/internal/skema/{id}/units")
    public ResponseEntity<?> getSkemaUnits(@PathVariable Long id) {
        Skema skema = skemaRepository.findById(id).orElse(null);
        if (skema == null) return ResponseEntity.notFound().build();
        
        // Mengembalikan list unit yang berelasi dengan skema tersebut
        return ResponseEntity.ok(skema.getUnits()); 
    }
}