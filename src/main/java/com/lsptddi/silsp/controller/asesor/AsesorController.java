package com.lsptddi.silsp.controller.asesor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

class User {
    private String fullName;
    private String role;

    public User(String fullName, String role) {
        this.fullName = fullName;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}

@Controller
@RequestMapping("/asesor")
public class AsesorController {
   @ModelAttribute
    public void addGlobalAttributes(Model model) {
        User loggedInUser = new User("Muhammad Satria Arrozak", "Asesor");
        // Objek user tiruan
        // Di aplikasi nyata, data ini akan diambil dari user yang sedang login
        model.addAttribute("user", loggedInUser);

    }

    @GetMapping
    public String index(Model model) {

        return "pages/asesor/dashboard";
    }

    // ==============================================
    // Menu sidebar Jadwal Sertifikasi
    // ==============================================

    @GetMapping("/surat-tugas")
    public String showSertifikasiList(Model model) { // 1. Tambahkan Model sebagai parameter
        return "pages/asesor/suratTugas-list";
    }
    
}
