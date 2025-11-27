package com.lsptddi.silsp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.lsptddi.silsp.dto.UserRoleDto;

import java.util.List;

// Objek User tiruan untuk simulasi
// class User {
//     private String fullName;
//     private String role;

//     public User(String fullName, String role) {
//         this.fullName = fullName;
//         this.role = role;
//     }

//     public String getFullName() {
//         return fullName;
//     }

//     public String getRole() {
//         return role;
//     }
// }

@Controller
@RequestMapping("/admin")
public class AdminController {
    // @Autowired
    // private SidebarService sidebarService;

    @Autowired
    private UserRepository userRepository; // 1. Inject Repository

    // @ModelAttribute
    // public void addGlobalAttributes(Model model) {
    // User loggedInUser = new User("Muhammad Satria Arrozak", "Admin");
    // // Objek user tiruan
    // // Di aplikasi nyata, data ini akan diambil dari user yang sedang login
    // model.addAttribute("user", loggedInUser);

    // }

    @GetMapping("/dashboard")
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

    @GetMapping("/surat-tugas-asesor")
    public String showDataSuratTugas(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/surat/surat-tugas-list";
    }

    @GetMapping("/surat-tugas-asesor/add")
    public String showAddSuratTugas(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/surat/surat-tugas-add";
    }

    @GetMapping("/surat-tugas-asesor/view")
    public String showViewSuratTugas(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/surat/surat-tugas-view";
    }

    @GetMapping("/surat-tugas-asesor/edit")
    public String showEditSuratTugas(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/surat/surat-tugas-edit";
    }

    @GetMapping("/data-pengguna")
    public String dataPengguna(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // 1. Pageable tetap sama
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // 2. Panggil Repo method BARU (searchUserRoles)
        // Return type sekarang Page<UserRoleDto>
        Page<UserRoleDto> pageResult = userRepository.searchUserRoles(keyword, role, pageable);

        // 3. Kirim ke HTML
        model.addAttribute("listPengguna", pageResult.getContent()); // List DTO
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);

        return "pages/admin/users/users-list";

    }

    // @GetMapping("/data-pengguna")
    // public String showDataPengguna(Model model,
    // @RequestParam(value = "keyword", required = false) String keyword,
    // @RequestParam(value = "role", required = false) String role,
    // @RequestParam(value = "page", defaultValue = "0") int page, // Default
    // Halaman 1 (index 0)
    // @RequestParam(value = "size", defaultValue = "10") int size) { // Default 10
    // data

    // // 1. Buat Pageable (Urutkan berdasarkan ID DESC agar data terbaru diatas)
    // Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

    // // 2. Panggil Repo
    // Page<User> userPage = userRepository.searchUsers(keyword, role, pageable);

    // // 3. Kirim Data ke HTML
    // model.addAttribute("listPengguna", userPage.getContent()); // List Datanya
    // model.addAttribute("currentPage", page); // Halaman saat ini
    // model.addAttribute("totalPages", userPage.getTotalPages());// Total Halaman
    // model.addAttribute("totalItems", userPage.getTotalElements()); // Total semua
    // data
    // model.addAttribute("size", size); // Ukuran per halaman (5, 10, etc)

    // // Kirim balik filter agar tidak hilang
    // model.addAttribute("keyword", keyword);
    // model.addAttribute("selectedRole", role);

    // return "pages/admin/users/users-list";
    // }

    // @GetMapping("/data-pengguna")
    // public String showDataPengguna(Model model, @RequestParam(value = "keyword",
    // required = false) String keyword,
    // @RequestParam(value = "role", required = false) String role) { // 1.
    // Tambahkan Model sebagai parameter

    // // 1. Panggil Repository dengan parameter pencarian
    // List<User> users = userRepository.searchUsers(keyword, role);

    // // 2. Kirim hasil data ke HTML
    // model.addAttribute("listPengguna", users);

    // // 3. Kirim BALIK parameter ke HTML (Agar form tidak reset setelah disubmit)
    // model.addAttribute("keyword", keyword);
    // model.addAttribute("selectedRole", role);

    // // // 3. Masukkan ke dalam Model agar bisa dibaca di HTML
    // // model.addAttribute("listPengguna", users);
    // return "pages/admin/users/users-list";
    // }

    @GetMapping("/data-pengguna/tambah-users")
    public String showAddPengguna(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/users/users-add";
    }

    @GetMapping("/data-pengguna/view-users/{id}")

    public String detailUser(@PathVariable Long id,
            @RequestParam(value = "role", required = false) String role,
            Model model) {

        // 1. Cari User
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/data-pengguna";
        }

        // 2. Tentukan Role yang akan ditampilkan
        // Jika parameter role kosong (misal akses langsung), ambil role pertama user
        if (role == null && !user.getRoles().isEmpty()) {
            role = user.getRoles().iterator().next().getName();
        }

        // 3. Kirim ke View
        model.addAttribute("user", user);
        model.addAttribute("viewRole", role); // String: "ADMIN", "ASESI", atau "ASESOR"

        return "pages/admin/users/users-view";
    }

    @GetMapping("/data-pengguna/edit-users")
    public String showEditPengguna(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/users/users-edit";
    }

    @GetMapping("/data-pengguna/delete/{id}")
    public String deletePengguna(@PathVariable Long id, RedirectAttributes attributes) {

        try {
            // Cek apakah user ada sebelum dihapus (Opsional, tapi bagus)
            if (!userRepository.existsById(id)) {
                attributes.addFlashAttribute("error", "Data pengguna tidak ditemukan!");
                return "redirect:/admin/data-pengguna";
            }

            // Lakukan penghapusan data
            userRepository.deleteById(id);

            // Kirim pesan sukses
            attributes.addFlashAttribute("success", "Data pengguna berhasil dihapus secara permanen!");
        } catch (Exception e) {
            // Tangani error, misal Foreign Key Constraint
            attributes.addFlashAttribute("error",
                    "Gagal menghapus pengguna. Pastikan data terkait (misal: data registrasi) sudah dihapus.");
        }
        return "redirect:/admin/data-pengguna";
    }

    @GetMapping("/data-asesi")
    public String showDataAsesi(Model model) { // 1. Tambahkan Model sebagai parameter
        // // Simulasi: Membuat objek user yang sedang login
        // User loggedInUser = new User("Muhammad Satria Arrozak", "Admin");

        // // 2. Tambahkan atribut yang diperlukan untuk layout
        // model.addAttribute("user", loggedInUser);
        // // model.addAttribute("pageTitle", "Data Asesi"); // Judul halaman diubah
        // sesuai
        // // konteks
        // // model.addAttribute("menuItems", sidebarService.getAdminMenuItems());

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