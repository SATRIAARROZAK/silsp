package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.model.Skema;
import com.lsptddi.silsp.repository.SkemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
public class ApiController {
    @Autowired private SkemaRepository skemaRepository;

    @GetMapping("/skema/{id}/units")
    public ResponseEntity<?> getSkemaUnits(@PathVariable Long id) {
        Skema skema = skemaRepository.findById(id).orElse(null);
        if (skema == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(skema.getUnits()); // Pastikan field units di Skema ter-load (EAGER atau Transactional)
    }
}