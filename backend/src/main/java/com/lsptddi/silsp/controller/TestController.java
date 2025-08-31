package com.lsptddi.silsp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Memberitahu Spring bahwa ini adalah kelas controller untuk API
public class TestController {

    // Anotasi @GetMapping("/api/test") berarti:
    // "Jika ada yang mengakses URL /api/test dengan metode GET, jalankan method ini"
    @GetMapping("/api/test")
    public String testBackend() {
        // Mengembalikan sebuah String sederhana.
        // Spring secara otomatis akan mengubahnya menjadi response teks.
        return "hi dev! Your backend is working fine.";
    }
}