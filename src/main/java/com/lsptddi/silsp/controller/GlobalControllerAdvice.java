package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.service.SidebarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice // Anotasi ini membuat kelas ini berlaku global untuk semua controller
public class GlobalControllerAdvice {

    @Autowired
    private SidebarService sidebarService;

    // Method ini akan dijalankan SEBELUM method controller manapun
    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // Logika untuk menentukan menu mana yang akan ditampilkan
        if (requestURI.startsWith("/admin")) {
            model.addAttribute("menuItems", sidebarService.getAdminMenuItems());
        } else if (requestURI.startsWith("/asesor")) {
            model.addAttribute("menuItems", sidebarService.getAsesorMenuItems());
        } else if (requestURI.startsWith("/asesi")) {
            model.addAttribute("menuItems", sidebarService.getAsesiMenuItems());
        }
        // Tambahkan logika untuk role lain di sini
    }
}