package com.lsptddi.silsp.controller.admin;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
// import com.lsptddi.silsp.service.SidebarService;

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
    // @Autowired
    // private SidebarService sidebarService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        User loggedInUser = new User("Muhammad Satria Arrozak", "Admin");
        // Objek user tiruan
        // Di aplikasi nyata, data ini akan diambil dari user yang sedang login
        model.addAttribute("user", loggedInUser);

    }

    @GetMapping
    public String index(Model model) {

        return "pages/admin/dashboard";
    }

    
    // ==============================================
    // Menu sidebar Skema Sertifikasi
    // ==============================================

    @GetMapping("/skema")
    public String showSkemaList(Model model) { // 1. Tambahkan Model sebagai parameter
        return "pages/admin/skema/skema-list";
    }

    @GetMapping("/skema/tambah-skema")
    public String showSkemaTambahPage(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/skema/skema-add";
    }

    @GetMapping("/skema/edit-skema")
    public String showSkemaEditPage(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/skema/skema-edit";
    }

    @GetMapping("/skema/view-skema")
    public String showSkemaViewPage(Model model) { // 1. Tambahkan Model sebagai parameter
     
        return "pages/admin/skema/skema-view";
    }

    @GetMapping("/tuk")
    public String showDataTuk(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/tuk/tuk-list";
    }

    @GetMapping("/tuk/tambah-tuk")
    public String showAddTuk(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/tuk/tuk-add";
    }

    @GetMapping("/tuk/view-tuk")
    public String showViewTuk(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/tuk/tuk-view";
    }

    @GetMapping("/tuk/edit-tuk")
    public String showEditTuk(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/tuk/tuk-edit";
    }

    @GetMapping("/jadwal-sertifikasi")
    public String showDataJadwalAsesmen(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/jadwal/sertifikasi-list";
    }

    @GetMapping("/jadwal-sertifikasi/jadwal-tambah")
    public String showAddJadwalAsesmen(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/jadwal/sertifikasi-add";
    }

    @GetMapping("/jadwal-sertifikasi/jadwal-view")
    public String showViewJadwalAsesmen(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/jadwal/sertifikasi-view";
    }

    @GetMapping("/jadwal-sertifikasi/jadwal-edit")
    public String showEditJadwalAsesmen(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/jadwal/sertifikasi-edit";
    }

    @GetMapping("/data-pengguna")
    public String showDataPengguna(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/users/users-list";
    }

    @GetMapping("/data-pengguna/tambah-users")
    public String showAddPengguna(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/users/users-add";
    }

    @GetMapping("/data-pengguna/view-users")
    public String showViewPengguna(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/users/users-view";
    }

    @GetMapping("/data-pengguna/edit-users")
    public String showEditPengguna(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/users/users-edit";
    }

    @GetMapping("/data-asesi")
    public String showDataAsesi(Model model) { // 1. Tambahkan Model sebagai parameter
        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Muhammad Satria Arrozak", "Admin");

        // 2. Tambahkan atribut yang diperlukan untuk layout
        model.addAttribute("user", loggedInUser);
        // model.addAttribute("pageTitle", "Data Asesi"); // Judul halaman diubah sesuai
        // konteks
        // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());

        // 3. Kembalikan nama view yang benar
        return "pages/admin/asesi/asesi-list";
    }

    @GetMapping("/data-asesi/tambah-asesi")
    public String showAsesiTambahPage(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/asesi/asesi-add";
    }

    @GetMapping("/data-asesi/edit-asesi")
    public String showAsesiEditPage(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/asesi/asesi-edit";
    }

    @GetMapping("/data-asesi/view-asesi")
    public String showAsesiViewPage(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/asesi/asesi-view";
    }



    // @GetMapping("/asesi")
    // public String showAsesiListPage(Model model) {
    // // ... (logika data asesi Anda) ...

    // // Tambahkan daftar menu ke model
    // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());
    // return "pages/admin/asesi-list";
    // }
}