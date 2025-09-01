package com.lsptddi.silsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Gunakan @Controller, bukan @RestController
public class DashboardController {

    // Jika ada yang mengakses URL root "/", jalankan method ini
    @GetMapping("/")
    public String showDashboard(Model model) {
        // 1. Menyiapkan data yang akan dikirim ke tampilan
        String pesan = "Hello ini dari Backend (Monolitik)";

        // 2. "Membungkus" data ke dalam objek Model
        // "pesanDariServer" adalah nama variabel yang akan digunakan di HTML
        model.addAttribute("pesanDariServer", pesan);

        // 3. Mengembalikan nama file HTML yang ada di folder templates
        // Tidak perlu menulis .html, cukup namanya saja.
        return "dashboard"; 
    }
}