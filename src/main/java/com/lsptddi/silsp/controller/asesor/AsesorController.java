package com.lsptddi.silsp.controller.asesor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/dashboard")
    public String showAsesorDashboard(Model model) {
          User loggedInUser = new User("Satria Arrozak (Asesor)", "ASESOR");

        // Mengirim objek user dan judul halaman ke frontend
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pageTitle", "Admin Dashboard");


        return "layouts/asesor/dashboard";
    }
    
}
