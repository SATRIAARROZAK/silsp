package com.lsptddi.silsp.controller.admin;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public String showAdminDashboard(Model model) {

        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Satria Arrozak", "ADMIN");

        // Mengirim objek user dan judul halaman ke frontend
        model.addAttribute("user", loggedInUser);

        return "pages/admin/dashboard";
    }

    @GetMapping("/data-asesi")
    public String index(Model model) { // 1. Tambahkan Model sebagai parameter
        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Satria Arrozak", "ADMIN");

        // 2. Tambahkan atribut yang diperlukan untuk layout
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pageTitle", "Data Asesi"); // Judul halaman diubah sesuai konteks
        // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());

        // 3. Kembalikan nama view yang benar
        return "pages/admin/asesi/asesi-list";
    }

     // Removing duplicate method as it already exists above

    @GetMapping("/edit-data-asesi")
    public String showAsesiEditPage (Model model) { // 1. Tambahkan Model sebagai parameter
        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Satria Arrozak", "ADMIN");

        // 2. Tambahkan atribut yang diperlukan untuk layout
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pageTitle", "Data Asesi"); // Judul halaman diubah sesuai konteks
        // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());

        // 3. Kembalikan nama view yang benar
        return "pages/admin/asesi/asesi-edit";
    }

    
     @GetMapping("/skema")
    public String showSkemaList(Model model) { // 1. Tambahkan Model sebagai parameter
        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Satria Arrozak", "ADMIN");

        // 2. Tambahkan atribut yang diperlukan untuk layout
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pageTitle", "Data Skema"); // Judul halaman diubah sesuai konteks
        // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());

        // 3. Kembalikan nama view yang benar
        return "pages/admin/skema/skema-list";
    }
    @GetMapping("/edit-skema")
    public String showSkemaEditPage (Model model) { // 1. Tambahkan Model sebagai parameter
        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Satria Arrozak", "ADMIN");

        // 2. Tambahkan atribut yang diperlukan untuk layout
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pageTitle", "Data Skema"); // Judul halaman diubah sesuai konteks
        // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());

        // 3. Kembalikan nama view yang benar
        return "pages/admin/skema/skema-edit";
    }

    @GetMapping("/tambah-skema")  
    public String showSkemaTambahPage (Model model) { // 1. Tambahkan Model sebagai parameter
        // Simulasi: Membuat objek user yang sedang login
        User loggedInUser = new User("Satria Arrozak", "ADMIN");

        // 2. Tambahkan atribut yang diperlukan untuk layout
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pageTitle", "Data Skema"); // Judul halaman diubah sesuai konteks
        // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());

        // 3. Kembalikan nama view yang benar
        return "pages/admin/skema/skema-add";
    }

    // @GetMapping("/asesi")
    // public String showAsesiListPage(Model model) {
    // // ... (logika data asesi Anda) ...

    // // Tambahkan daftar menu ke model
    // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());
    // return "pages/admin/asesi-list";
    // }
}