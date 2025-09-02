package com.lsptddi.silsp.controller.admin;

import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        // Cukup arahkan ke "pages/admin/dashboard"
        return "pages/admin/dashboard";
    }
}