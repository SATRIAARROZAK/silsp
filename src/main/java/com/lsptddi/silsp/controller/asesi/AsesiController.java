package com.lsptddi.silsp.controller.asesi;

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
@RequestMapping("/asesi")
public class AsesiController {
  
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        User loggedInUser = new User("Muhammad Satria Arrozak", "Asesi");
        // Objek user tiruan
        // Di aplikasi nyata, data ini akan diambil dari user yang sedang login
        model.addAttribute("user", loggedInUser);

    }

    @GetMapping
    public String index(Model model) {

        return "pages/asesi/dashboard";
    }

    // ==============================================
    // Menu sidebar Jadwal Sertifikasi
    // ==============================================

    @GetMapping("/daftar-sertifikasi")
    public String showSertifikasiList(Model model) { // 1. Tambahkan Model sebagai parameter
        return "pages/asesi/sertifikasi/sertifikasi-list";
    }

}
