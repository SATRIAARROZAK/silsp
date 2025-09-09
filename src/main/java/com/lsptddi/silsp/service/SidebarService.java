package com.lsptddi.silsp.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.lsptddi.silsp.dto.MenuItemDTO;

@Service
public class SidebarService {

    // Method ini akan menghasilkan menu untuk peran ADMIN
    public List<MenuItemDTO> getAdminMenuItems() {
        List<MenuItemDTO> menuItems = new ArrayList<>();
        menuItems.add(new MenuItemDTO("Dashboard", "/admin/dashboard", "fas fa-tachometer-alt"));
        // menuItems.add(new MenuItemDTO("Data Asesi", "/admin/asesi", "fas fa-users"));
        menuItems.add(new MenuItemDTO("Kelola Skema", "#", "fas fa-list")); // Contoh link non-aktif
        // Tambahkan menu admin lainnya di sini
        return menuItems;
    }

    // Method ini akan menghasilkan menu untuk peran ASESOR
    public List<MenuItemDTO> getAsesorMenuItems() {
        List<MenuItemDTO> menuItems = new ArrayList<>();
        menuItems.add(new MenuItemDTO("Dashboard", "/asesor/dashboard", "fas fa-tachometer-alt"));
        menuItems.add(new MenuItemDTO("Penilaian Asesi", "#", "fas fa-check-square"));
        // Tambahkan menu asesor lainnya di sini
        return menuItems;
    }

    // Method ini akan menghasilkan menu untuk peran ASESI
    public List<MenuItemDTO> getAsesiMenuItems() {
        List<MenuItemDTO> menuItems = new ArrayList<>();
        menuItems.add(new MenuItemDTO("Dashboard", "/asesi/dashboard", "fas fa-tachometer-alt"));
        menuItems.add(new MenuItemDTO("Pendaftaran Saya", "#", "fas fa-file-alt"));
        // Tambahkan menu asesi lainnya di sini
        return menuItems;
    }

}
