package com.lsptddi.silsp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lsptddi.silsp.repository.RefEducationRepository;
import com.lsptddi.silsp.repository.RefJobTypeRepository;
import com.lsptddi.silsp.repository.RoleRepository;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lsptddi.silsp.dto.UserDto;
import com.lsptddi.silsp.dto.UserRoleDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.lsptddi.silsp.model.*;
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

    @Autowired
    private RoleRepository roleRepository; // <--- WAJIB DI-INJECT
    @Autowired
    private RefEducationRepository educationRepository;
    @Autowired
    private RefJobTypeRepository jobTypeRepository;

    // Jika Anda menggunakan Spring Security, inject PasswordEncoder
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/data-pengguna/tambah-users")
    public String showAddPengguna(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/users/users-add";
    }

    @PostMapping("/data-pengguna/save")
    @ResponseBody
    public ResponseEntity<?> saveUser(@ModelAttribute UserDto userDto) {
        // public String saveUser(@ModelAttribute UserDto userDto) {
        User user = new User();

        // 1. DATA AKUN DASAR
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        // Password Default (Harusnya di-encode)
        user.setPassword(passwordEncoder.encode("12345678"));
        // user.setPassword(passwordEncoder.encode("123456"));
        // user.setPassword("{noop}123456"); // Contoh tanpa encoder sementara

        // ==========================================
        // PERBAIKAN: LOGIKA SIMPAN ROLE
        // ==========================================
        Set<Role> roles = new HashSet<>();

        // Cek apakah user memilih role di form?
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            for (String roleName : userDto.getRoles()) {
                // Cari Role di database berdasarkan nama ("ADMIN", "ASESI", dll)
                Role role = roleRepository.findByName(roleName).orElse(null);

                if (role != null) {
                    roles.add(role);
                } else {
                    // Opsional: Handle jika role tidak ditemukan di DB
                    System.out.println("Role tidak ditemukan: " + roleName);
                }
            }
        }
        // Set Role ke Entity User
        user.setRoles(roles);
        // ==========================================

        // 2. DATA PRIBADI & LAINNYA (Sama seperti sebelumnya)
        user.setFullName(userDto.getFullName());
        user.setBirthPlace(userDto.getBirthPlace());
        user.setBirthDate(userDto.getBirthDate());
        user.setGender(userDto.getGender());
        user.setNik(userDto.getNik());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddress(userDto.getAddress());
        user.setPostalCode(userDto.getPostalCode());
        user.setCitizenship(userDto.getCitizenship());
        user.setNoMet(userDto.getNoMet());

        user.setCompanyName(userDto.getCompanyName());
        user.setPosition(userDto.getPosition());
        user.setOfficePhone(userDto.getOfficePhone());
        user.setOfficeEmail(userDto.getOfficeEmail());
        user.setOfficeFax(userDto.getOfficeFax());
        user.setOfficeAddress(userDto.getOfficeAddress());

        // 3. RELASI 3NF (ID ke Object)
        if (userDto.getEducationId() != null) {
            user.setEducationId(educationRepository.findById(userDto.getEducationId()).orElse(null));
        }
        if (userDto.getJobTypeId() != null) {
            user.setJobTypeId(jobTypeRepository.findById(userDto.getJobTypeId()).orElse(null));
        }

        // 4. WILAYAH & SIGNATURE
        user.setProvinceId(userDto.getProvinceId());
        user.setCityId(userDto.getCityId());
        user.setDistrictId(userDto.getDistrictId());
        user.setSubDistrictId(userDto.getSubDistrictId());
        user.setSignatureBase64(userDto.getSignatureBase64());

        userRepository.save(user);
        return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Pengguna berhasil disimpan\"}");
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

    // --- FITUR HALAMAN EDIT PENGGUNA ---
    // @GetMapping("/data-pengguna/edit-users/{id}")
    // public String updateUser(@PathVariable Long id, Model model) {
    // User user = userRepository.findById(id).orElse(null);
    // if (user == null) {
    // return "redirect:/admin/data-pengguna";
    // }
    // model.addAttribute("user", user);
    // return "pages/admin/users/users-edit";
    // }

    // ==========================================
    // FITUR EDIT PENGGUNA (DATA AKUN)
    // ==========================================

    // 1. TAMPILKAN FORM EDIT (GET)
    @GetMapping("/data-pengguna/edit-users/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        // A. Cari User Lama
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/data-pengguna";
        }

        // B. Pindahkan Data Entity ke DTO (Mapping)
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());

        // --- DATA AKUN ---
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        // Mapping Role (Set<Role> -> List<String>)
        // Agar dropdown Select2 otomatis terpilih
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        userDto.setRoles(roleNames);

        // --- DATA LAINNYA (Mapping agar tidak hilang saat ditampilkan) ---
        // Penting: Masukkan data ini agar saat form diload, tab data pribadi juga
        // terisi
        userDto.setFullName(user.getFullName());
        userDto.setBirthPlace(user.getBirthPlace());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setGender(user.getGender());
        userDto.setNik(user.getNik());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setAddress(user.getAddress());
        userDto.setPostalCode(user.getPostalCode());
        userDto.setCitizenship(user.getCitizenship());
        userDto.setNoMet(user.getNoMet());

        userDto.setCompanyName(user.getCompanyName());
        userDto.setPosition(user.getPosition());
        userDto.setOfficePhone(user.getOfficePhone());
        userDto.setOfficeEmail(user.getOfficeEmail());
        userDto.setOfficeFax(user.getOfficeFax());
        userDto.setOfficeAddress(user.getOfficeAddress());

        // Mapping Relasi ID (3NF)
        if (user.getEducationId() != null)
            userDto.setEducationId(user.getEducationId().getId());
        if (user.getJobTypeId() != null)
            userDto.setJobTypeId(user.getJobTypeId().getId());

        // Mapping Wilayah
        userDto.setProvinceId(user.getProvinceId());
        userDto.setCityId(user.getCityId());
        userDto.setDistrictId(user.getDistrictId());
        userDto.setSubDistrictId(user.getSubDistrictId());

        userDto.setSignatureBase64(user.getSignatureBase64());

        // C. Kirim ke View
        model.addAttribute("userDto", userDto);

        return "pages/admin/users/users-edit"; // File HTML baru
    }

    // 2. PROSES UPDATE (POST)
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute UserDto userDto, RedirectAttributes attributes) {
        try {
            // A. Ambil User Lama dari Database berdasarkan ID (Hidden Input)
            User user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // B. UPDATE DATA AKUN
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());

            // C. LOGIKA PASSWORD (Hanya update jika diisi)
            // Jika kosong, berarti user tidak ingin mengganti password lama
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            // D. UPDATE ROLE
            if (userDto.getRoles() != null) {
                Set<Role> newRoles = new HashSet<>();
                for (String roleName : userDto.getRoles()) {
                    roleRepository.findByName(roleName).ifPresent(newRoles::add);
                }
                user.setRoles(newRoles);
            }

            // E. UPDATE DATA PRIBADI & LAINNYA
            // (Kita update juga agar sinkron dengan form edit yang mungkin diubah user)
            user.setFullName(userDto.getFullName());
            user.setBirthPlace(userDto.getBirthPlace());
            user.setBirthDate(userDto.getBirthDate());
            user.setGender(userDto.getGender());
            user.setNik(userDto.getNik());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setAddress(userDto.getAddress());
            user.setPostalCode(userDto.getPostalCode());
            user.setCitizenship(userDto.getCitizenship());
            user.setNoMet(userDto.getNoMet());

            user.setCompanyName(userDto.getCompanyName());
            user.setPosition(userDto.getPosition());
            user.setOfficePhone(userDto.getOfficePhone());
            user.setOfficeEmail(userDto.getOfficeEmail());
            user.setOfficeFax(userDto.getOfficeFax());
            user.setOfficeAddress(userDto.getOfficeAddress());

            // Relasi
            if (userDto.getEducationId() != null) {
                user.setEducationId(educationRepository.findById(userDto.getEducationId()).orElse(null));
            }
            if (userDto.getJobTypeId() != null) {
                user.setJobTypeId(jobTypeRepository.findById(userDto.getJobTypeId()).orElse(null));
            }

            // Wilayah & Tanda Tangan
            user.setProvinceId(userDto.getProvinceId());
            user.setCityId(userDto.getCityId());
            user.setDistrictId(userDto.getDistrictId());
            user.setSubDistrictId(userDto.getSubDistrictId());

            // Signature: Hanya update jika ada tanda tangan baru, jika kosong biarkan yang
            // lama
            if (userDto.getSignatureBase64() != null && !userDto.getSignatureBase64().isEmpty()) {
                user.setSignatureBase64(userDto.getSignatureBase64());
            }

            // F. SIMPAN PERUBAHAN
            userRepository.save(user);

            attributes.addFlashAttribute("success", "Data pengguna berhasil diperbarui!");

        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Gagal mengupdate data: " + e.getMessage());
            return "redirect:/admin/users/users-edit/" + userDto.getId(); // Kembali ke form edit jika gagal
        }

        return "redirect:/admin/data-pengguna";
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