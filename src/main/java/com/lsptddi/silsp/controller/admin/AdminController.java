package com.lsptddi.silsp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.lsptddi.silsp.service.SidebarService;


// Objek User tiruan untuk simulasi
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
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private SidebarService sidebarService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {

        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Satria Arrozak (Admin)", "ADMIN");

        // Mengirim objek user dan judul halaman ke frontend
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pageTitle", "Admin Dashboard");

        return "layouts/admin/dashboard";
    }

    @GetMapping("/asesi")
    public String showAsesiListPage(Model model) {
        // ... (logika data asesi Anda) ...

        // Tambahkan daftar menu ke model
        model.addAttribute("menuItems", sidebarService.getAdminMenuItems());
        return "pages/admin/asesi-list";
    }
}