package com.lsptddi.silsp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import com.lsptddi.silsp.dto.SkemaDto;
import com.lsptddi.silsp.dto.TukDto;
import com.lsptddi.silsp.dto.UserDto;
import com.lsptddi.silsp.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Pastikan ada ini
import com.lsptddi.silsp.repository.TypeEducationRepository;
import com.lsptddi.silsp.repository.TypePekerjaanRepository;
import com.lsptddi.silsp.repository.TypePengajuanSkemaRepository;
import com.lsptddi.silsp.repository.TypeSkemaRepository;
import com.lsptddi.silsp.repository.TypeTukRepository;
import com.lsptddi.silsp.repository.RoleRepository;
import com.lsptddi.silsp.repository.SkemaRepository;
import com.lsptddi.silsp.repository.TukRepository;
import com.lsptddi.silsp.repository.UserRepository;
import com.lsptddi.silsp.service.TukService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.lsptddi.silsp.dto.UserRoleDto;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.*;
import java.security.Principal;

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
    private TypeEducationRepository educationRepository;
    @Autowired
    private TypePekerjaanRepository jobTypeRepository;

    // Jika Anda menggunakan Spring Security, inject PasswordEncoder
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SkemaRepository schemaRepository;
    @Autowired
    private TypeSkemaRepository schemaTypeRepository;
    @Autowired
    private TypePengajuanSkemaRepository schemaModeRepository;

    private final String UPLOAD_DIR = "uploads/skema/"; // Pastikan folder ini ada

    @Autowired
    private TukRepository tukRepository;
    @Autowired
    private TypeTukRepository tukTypeRepository;
    @Autowired
    private TukService tukService;

    // Di dalam class AsesiController

    @GetMapping("/dashboard")
    public String index(Model model) {

        return "pages/admin/dashboard";
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            model.addAttribute("user", user);

            // --- TAMBAHAN PENTING ---
            // Ini kuncinya: Kirim sinyal bahwa halaman ini adalah konteks ADMIN
            model.addAttribute("currentRole", "Admin");
        }
    }

    @GetMapping("/api/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username, @RequestParam(required = false) Long id) {
        // Logika: Return TRUE jika valid (tersedia), FALSE jika invalid (sudah ada)
        if (id != null) {
            // Mode EDIT: Cek duplikat selain punya sendiri
            return !userRepository.existsByUsernameAndIdNot(username, id);
        } else {
            // Mode ADD: Cek duplikat biasa
            return !userRepository.existsByUsername(username);
        }
    }

    // API Cek Email
    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email, @RequestParam(required = false) Long id) {
        if (id != null) {
            return !userRepository.existsByEmailAndIdNot(email, id);
        } else {
            return !userRepository.existsByEmail(email);
        }
    }

    // ==============================================
    // Menu sidebar Skema Sertifikasi
    // ==============================================

    @GetMapping("/skema")
    public String showSkemaList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Pagination & Sorting (Terbaru diatas)
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Panggil Repository
        Page<Skema> schemaPage = schemaRepository.searchSchema(keyword, pageable);

        // Kirim Data ke View
        model.addAttribute("listSkema", schemaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", schemaPage.getTotalPages());
        model.addAttribute("totalItems", schemaPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        return "pages/admin/skema/skema-list";
    }

    @GetMapping("/skema/tambah-skema")
    public String showSkemaTambahPage(Model model) { // 1. Tambahkan Model sebagai parameter

        // Load data referensi untuk dropdown
        model.addAttribute("types", schemaTypeRepository.findAll());
        model.addAttribute("modes", schemaModeRepository.findAll());
        return "pages/admin/skema/skema-add";
    }

    @PostMapping("/skema/save")
    @ResponseBody
    public ResponseEntity<?> saveSchema(@ModelAttribute SkemaDto dto) {
        try {
            Skema schema = new Skema();

            // A. Mapping Data Dasar
            schema.setName(dto.getNamaSkema());
            schema.setCode(dto.getKodeSkema());
            schema.setLevel(dto.getLevel());
            schema.setNoSkkni(dto.getNoSkkni());
            schema.setSkkniYear(dto.getTahunSkkni());
            schema.setEstablishmentDate(dto.getTanggalPenetapan());

            // B. Mapping Relasi (3NF)
            if (dto.getJenisSkemaId() != null) {
                schema.setSchemaType(schemaTypeRepository.findById(dto.getJenisSkemaId()).orElse(null));
            }
            if (dto.getModeSkemaId() != null) {
                schema.setSchemaMode(schemaModeRepository.findById(dto.getModeSkemaId()).orElse(null));
            }

            // C. Upload File
            if (dto.getFileSkema() != null && !dto.getFileSkema().isEmpty()) {
                String fileName = saveFile(dto.getFileSkema());
                schema.setDocumentPath(fileName);
            }

            // D. Mapping Unit Skema (Looping Array)
            if (dto.getKodeUnit() != null) {
                for (int i = 0; i < dto.getKodeUnit().size(); i++) {
                    UnitSkema unit = new UnitSkema();
                    unit.setCode(dto.getKodeUnit().get(i));
                    unit.setTitle(dto.getJudulUnit().get(i));
                    schema.addUnit(unit); // Helper method handles relationship
                }
            }

            // E. Mapping Persyaratan (Looping Array)
            if (dto.getPersyaratan() != null) {
                for (String reqText : dto.getPersyaratan()) {
                    if (reqText != null && !reqText.trim().isEmpty()) {
                        PersyaratanSkema req = new PersyaratanSkema();
                        req.setDescription(reqText);
                        schema.addRequirement(req);
                    }
                }
            }

            // Simpan Parent (Cascade akan menyimpan Unit & Req)
            schemaRepository.save(schema);

            return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Skema berhasil disimpan!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal menyimpan: " + e.getMessage() + "\"}");
        }
    }

    // Helper untuk simpan file
    private String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return fileName;
    }

    @GetMapping("/skema/edit/{id}")
    public String editSchema(@PathVariable Long id, Model model) {
        Skema schema = schemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schema Id:" + id));

        // Kirim data Schema (Entity) langsung ke View
        // Thymeleaf akan mengambil data dari Entity ini
        model.addAttribute("skema", schema);

        // Kirim Data Referensi untuk Dropdown
        model.addAttribute("types", schemaTypeRepository.findAll());
        model.addAttribute("modes", schemaModeRepository.findAll());

        return "pages/admin/skema/skema-edit";
    }

    // 6. PROSES UPDATE (POST AJAX)
    @PostMapping("/skema/update")
    @ResponseBody
    public ResponseEntity<?> updateSchema(@ModelAttribute SkemaDto dto) {
        try {
            Skema schema = schemaRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Skema tidak ditemukan"));

            // A. Update Data Dasar
            schema.setName(dto.getNamaSkema());
            schema.setCode(dto.getKodeSkema());
            schema.setLevel(dto.getLevel());
            schema.setNoSkkni(dto.getNoSkkni());
            schema.setSkkniYear(dto.getTahunSkkni());
            schema.setEstablishmentDate(dto.getTanggalPenetapan());

            // B. Update Relasi Master (Jenis & Mode)
            if (dto.getJenisSkemaId() != null) {
                schema.setSchemaType(schemaTypeRepository.findById(dto.getJenisSkemaId()).orElse(null));
            }
            if (dto.getModeSkemaId() != null) {
                schema.setSchemaMode(schemaModeRepository.findById(dto.getModeSkemaId()).orElse(null));
            }

            // C. Update File (Hanya jika ada upload baru)
            if (dto.getFileSkema() != null && !dto.getFileSkema().isEmpty()) {
                // Hapus file lama jika perlu (opsional)
                // ... logic hapus file lama ...

                String fileName = saveFile(dto.getFileSkema());
                schema.setDocumentPath(fileName);
            }

            // D. Update Unit Skema (Strategi: Clear & Add All)
            schema.getUnits().clear(); // Hapus unit lama
            if (dto.getKodeUnit() != null) {
                for (int i = 0; i < dto.getKodeUnit().size(); i++) {
                    UnitSkema unit = new UnitSkema();
                    unit.setCode(dto.getKodeUnit().get(i));
                    unit.setTitle(dto.getJudulUnit().get(i));
                    schema.addUnit(unit); // Tambahkan yang baru
                }
            }

            // E. Update Persyaratan (Strategi: Clear & Add All)
            schema.getRequirements().clear(); // Hapus req lama
            if (dto.getPersyaratan() != null) {
                for (String reqText : dto.getPersyaratan()) {
                    if (reqText != null && !reqText.trim().isEmpty()) {
                        PersyaratanSkema req = new PersyaratanSkema();
                        req.setDescription(reqText);
                        schema.addRequirement(req);
                    }
                }
            }

            schemaRepository.save(schema);

            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"Data skema berhasil diperbarui!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal update: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/skema/view/{id}")
    public String viewSchema(@PathVariable Long id, Model model) {
        Skema schema = schemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skema tidak ditemukan dengan ID: " + id));

        model.addAttribute("skema", schema);

        return "pages/admin/skema/skema-view";
    }

    // 7. HAPUS SKEMA (GET)
    @GetMapping("/skema/delete/{id}")
    public String deleteSchema(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            // Cek apakah data ada
            if (!schemaRepository.existsById(id)) {
                attributes.addFlashAttribute("error", "Data skema tidak ditemukan!");
                return "redirect:/admin/skema";
            }

            // HAPUS DATA
            // Karena di Entity Schema sudah ada CascadeType.ALL pada 'units' dan
            // 'requirements',
            // maka data anak (Unit & Persyaratan) akan ikut terhapus otomatis.
            schemaRepository.deleteById(id);

            attributes.addFlashAttribute("success", "Skema beserta unit dan persyaratan berhasil dihapus.");
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Gagal menghapus skema. Masalah: " + e.getMessage());
        }

        return "redirect:/admin/skema";
    }

    // @GetMapping("/skema/delete/{id}")
    // public String deleteSkema(@PathVariable Long id, RedirectAttributes
    // attributes) {

    // return "redirect:/admin/skema";
    // }

    @GetMapping("/tuk")
    public String listTuk(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Tuk> tukPage = tukRepository.searchTuk(keyword, pageable);

        model.addAttribute("listTuk", tukPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tukPage.getTotalPages());
        model.addAttribute("totalItems", tukPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        return "pages/admin/tuk/tuk-list";
    }

    @GetMapping("/tuk/tambah-tuk")
    public String showAddTuk(Model model) { // 1. Tambahkan Model sebagai parameter

        model.addAttribute("types", tukTypeRepository.findAll());
        // Kirim kode baru ke view (untuk ditampilkan di input readonly)
        model.addAttribute("newCode", tukService.generateTukCode());
        return "pages/admin/tuk/tuk-add";
    }

    // SAVE (AJAX)
    @PostMapping("/tuk/save")
    @ResponseBody
    public ResponseEntity<?> saveTuk(@ModelAttribute TukDto dto) {
        try {
            Tuk tuk = new Tuk();
            tuk.setName(dto.getNamaTuk());

            // Generate Kode di Server (Lebih Aman daripada kirim dari form)
            tuk.setCode(tukService.generateTukCode());

            if (dto.getJenisTukId() != null) {
                tuk.setTukType(tukTypeRepository.findById(dto.getJenisTukId()).orElse(null));
            }

            tuk.setPhoneNumber(dto.getNoTelp());
            tuk.setEmail(dto.getEmail());
            tuk.setAddress(dto.getAlamat());
            tuk.setProvinceId(dto.getProvinceId());
            tuk.setCityId(dto.getCityId());

            tukRepository.save(tuk);

            return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"TUK berhasil disimpan!\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/tuk/view/{id}")
    public String viewTuk(@PathVariable Long id, Model model) {
        // Cari TUK berdasarkan ID
        Tuk tuk = tukRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TUK tidak ditemukan dengan ID: " + id));

        // Kirim object 'tuk' ke HTML
        model.addAttribute("tuk", tuk);
        return "pages/admin/tuk/tuk-view";
    }

    @GetMapping("/tuk/edit/{id}")
    public String editTuk(@PathVariable Long id, Model model) {
        Tuk tuk = tukRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TUK tidak ditemukan: " + id));

        // Mapping Entity ke DTO
        TukDto dto = new TukDto();
        dto.setId(tuk.getId());
        dto.setNamaTuk(tuk.getName());
        dto.setKodeTuk(tuk.getCode());
        dto.setNoTelp(tuk.getPhoneNumber());
        dto.setEmail(tuk.getEmail());
        dto.setAlamat(tuk.getAddress());
        dto.setProvinceId(tuk.getProvinceId());
        dto.setCityId(tuk.getCityId());

        if (tuk.getTukType() != null) {
            dto.setJenisTukId(tuk.getTukType().getId());
        }

        model.addAttribute("tukDto", dto);
        model.addAttribute("types", tukTypeRepository.findAll()); // Untuk dropdown jenis

        return "pages/admin/tuk/tuk-edit";
    }

    // 4. PROSES UPDATE (POST AJAX)
    @PostMapping("/tuk/update")
    @ResponseBody
    public ResponseEntity<?> updateTuk(@ModelAttribute TukDto dto) {
        try {
            Tuk tuk = tukRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("TUK tidak ditemukan"));

            // Update Data
            tuk.setName(dto.getNamaTuk());
            // Kode TUK biasanya tidak diubah (tetap), jadi tidak perlu setCode lagi

            if (dto.getJenisTukId() != null) {
                tuk.setTukType(tukTypeRepository.findById(dto.getJenisTukId()).orElse(null));
            }

            tuk.setPhoneNumber(dto.getNoTelp());
            tuk.setEmail(dto.getEmail());
            tuk.setAddress(dto.getAlamat());
            tuk.setProvinceId(dto.getProvinceId());
            tuk.setCityId(dto.getCityId());

            tukRepository.save(tuk);

            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"Data TUK berhasil diperbarui!\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal update: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/tuk/delete/{id}")
    public String deleteTuk(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            // Cek apakah data ada
            if (!tukRepository.existsById(id)) {
                attributes.addFlashAttribute("error", "Data TUK tidak ditemukan!");
            } else {
                // Hapus data
                tukRepository.deleteById(id);
                attributes.addFlashAttribute("success", "Data TUK berhasil dihapus permanen!");
            }
        } catch (Exception e) {
            // Menangani error jika TUK sedang dipakai (Relasi Foreign Key)
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Gagal menghapus TUK. Data mungkin sedang digunakan.");
        }

        // Redirect kembali ke halaman list
        return "redirect:pages/admin/tuk/tuk-list";
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
    public ResponseEntity<?> saveUser(@ModelAttribute UserDto userDto,
            @RequestParam("roles") List<String> roles,
            RedirectAttributes redirectAttributes) {
        try {
            User user = new User();
            // Cek Duplikat Username
            if (userRepository.existsByUsername(userDto.getUsername())) {
                // Perhatikan key "field": "username"
                return ResponseEntity.badRequest().body(
                        "{\"status\": \"error\", \"field\": \"username\", \"message\": \"Username sudah terdaftar\"}");
            }
            // Cek Duplikat Email
            if (userRepository.existsByEmail(userDto.getEmail())) {
                // Perhatikan key "field": "email"
                return ResponseEntity.badRequest()
                        .body("{\"status\": \"error\", \"field\": \"email\", \"message\": \"Email sudah terdaftar\"}");
            }
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode("12345678")); // Default password

            // --- LOGIKA BARU: BERSIHKAN FORMAT SEBELUM SIMPAN ---

            // 1. NIK: Hapus titik, simpan angka saja (32.73... -> 3273...)
            if (userDto.getNik() != null) {
                user.setNik(userDto.getNik().replaceAll("[^0-9]", ""));
            }

            // 2. NO TELP: Tambahkan '0' di depan (811... -> 0811...)
            if (userDto.getPhoneNumber() != null) {
                String rawPhone = userDto.getPhoneNumber().replaceAll("[^0-9]", ""); // Pastikan angka saja
                // Jika user iseng nulis 0 di depan, hapus dulu baru tambah 0 (biar ga double)
                if (rawPhone.startsWith("0"))
                    rawPhone = rawPhone.substring(1);
                user.setPhoneNumber("0" + rawPhone);
            }

            // 3. DATA PRIBADI LAIN
            user.setFullName(userDto.getFullName());
            user.setBirthPlace(userDto.getBirthPlace());
            user.setBirthDate(userDto.getBirthDate());
            user.setGender(userDto.getGender());
            user.setAddress(userDto.getAddress());
            user.setPostalCode(userDto.getPostalCode());
            user.setCitizenship(userDto.getCitizenship());
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

            // ... (Set Wilayah ID seperti kode sebelumnya) ...
            user.setProvinceId(userDto.getProvinceId());
            user.setCityId(userDto.getCityId());
            user.setDistrictId(userDto.getDistrictId());
            // user.setSubDistrictId(userDto.getSubDistrictId());
            user.setSignatureBase64(userDto.getSignatureBase64());

            // --- LOGIKA ROLE & DATA SPESIFIK ---
            Set<Role> roleSet = new HashSet<>();
            for (String roleName : roles) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                roleSet.add(role);

                // 4. KHUSUS ASESOR: NO MET
                // Format Input: 000.123456.2024 -> DB: MET.000.123456.2024
                if (roleName.equalsIgnoreCase("Asesor")) {
                    if (userDto.getNoMet() != null) {
                        user.setNoMet("MET." + userDto.getNoMet());
                    }
                    // ... (Set JobType) ...
                }
                // ... (Logic Asesi Citizenship) ...
            }
            user.setRoles(roleSet);

            userRepository.save(user);
            // redirectAttributes.addFlashAttribute("success", "Pengguna berhasil
            // ditambahkan!");
            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"Data berhasil ditambahkan\", \"id\": "
                            + user.getId()
                            + "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal: " + e.getMessage() + "\"}");
        }

        // e.printStackTrace();
        // redirectAttributes.addFlashAttribute("error", "Gagal menyimpan: " +
        // e.getMessage());
        // }
        // return "redirect:/admin/data-pengguna";
    }
    // public ResponseEntity<?> saveUser(@ModelAttribute UserDto userDto) {
    // // public String saveUser(@ModelAttribute UserDto userDto) {
    // User user = new User();

    // // Cek Duplikat Username
    // if (userRepository.existsByUsername(userDto.getUsername())) {
    // // Perhatikan key "field": "username"
    // return ResponseEntity.badRequest().body(
    // "{\"status\": \"error\", \"field\": \"username\", \"message\": \"Username
    // sudah terdaftar\"}");
    // }
    // // Cek Duplikat Email
    // if (userRepository.existsByEmail(userDto.getEmail())) {
    // // Perhatikan key "field": "email"
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"field\": \"email\", \"message\": \"Email
    // sudah terdaftar\"}");
    // }

    // // 1. DATA AKUN DASAR
    // user.setUsername(userDto.getUsername());
    // user.setEmail(userDto.getEmail());

    // // Password Default (Harusnya di-encode)
    // user.setPassword(passwordEncoder.encode("12345678"));
    // // user.setPassword(passwordEncoder.encode("123456"));
    // // user.setPassword("{noop}123456"); // Contoh tanpa encoder sementara

    // // ==========================================
    // // PERBAIKAN: LOGIKA SIMPAN ROLE
    // // ==========================================
    // Set<Role> roles = new HashSet<>();

    // // Cek apakah user memilih role di form?
    // if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
    // for (String roleName : userDto.getRoles()) {
    // // Cari Role di database berdasarkan nama ("ADMIN", "ASESI", dll)
    // Role role = roleRepository.findByName(roleName).orElse(null);

    // if (role != null) {
    // roles.add(role);
    // } else {
    // // Opsional: Handle jika role tidak ditemukan di DB
    // System.out.println("Role tidak ditemukan: " + roleName);
    // }
    // }
    // }
    // // Set Role ke Entity User
    // user.setRoles(roles);
    // // ==========================================

    // // 2. DATA PRIBADI & LAINNYA (Sama seperti sebelumnya)
    // user.setFullName(userDto.getFullName());
    // user.setBirthPlace(userDto.getBirthPlace());
    // user.setBirthDate(userDto.getBirthDate());
    // user.setGender(userDto.getGender());
    // user.setNik(userDto.getNik());
    // user.setPhoneNumber(userDto.getPhoneNumber());
    // user.setAddress(userDto.getAddress());
    // user.setPostalCode(userDto.getPostalCode());
    // user.setCitizenship(userDto.getCitizenship());
    // user.setNoMet(userDto.getNoMet());

    // user.setCompanyName(userDto.getCompanyName());
    // user.setPosition(userDto.getPosition());
    // user.setOfficePhone(userDto.getOfficePhone());
    // user.setOfficeEmail(userDto.getOfficeEmail());
    // user.setOfficeFax(userDto.getOfficeFax());
    // user.setOfficeAddress(userDto.getOfficeAddress());

    // // 3. RELASI 3NF (ID ke Object)
    // if (userDto.getEducationId() != null) {
    // user.setEducationId(educationRepository.findById(userDto.getEducationId()).orElse(null));
    // }
    // if (userDto.getJobTypeId() != null) {
    // user.setJobTypeId(jobTypeRepository.findById(userDto.getJobTypeId()).orElse(null));
    // }

    // // 4. WILAYAH & SIGNATURE
    // user.setProvinceId(userDto.getProvinceId());
    // user.setCityId(userDto.getCityId());
    // user.setDistrictId(userDto.getDistrictId());
    // user.setSubDistrictId(userDto.getSubDistrictId());
    // user.setSignatureBase64(userDto.getSignatureBase64());

    // userRepository.save(user);
    // return ResponseEntity.ok().body(
    // "{\"status\": \"success\", \"message\": \"Pengguna berhasil
    // disimpan\",\"id\": " + user.getId() + "}");

    // // userRepository.save(user);
    // // return ResponseEntity.ok().body("{\"status\": \"success\", \"message\":
    // // \"Pengguna berhasil disimpan\"}");
    // }

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
        // userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setAddress(user.getAddress());
        userDto.setPostalCode(user.getPostalCode());
        userDto.setCitizenship(user.getCitizenship());
        // userDto.setNoMet(user.getNoMet());

        // 2. NO TELP: Hapus '0' di depan agar di form jadi (811...)
        if (user.getPhoneNumber() != null && user.getPhoneNumber().startsWith("0")) {
            userDto.setPhoneNumber(user.getPhoneNumber().substring(1));
        } else {
            userDto.setPhoneNumber(user.getPhoneNumber());
        }

        // 3. NO MET: Hapus "MET." di depan
        if (user.getNoMet() != null) {
            userDto.setNoMet(user.getNoMet().replace("MET.", "").trim());
        }

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
        // userDto.setSubDistrictId(user.getSubDistrictId());

        userDto.setSignatureBase64(user.getSignatureBase64());

        // C. Kirim ke View
        model.addAttribute("userDto", userDto);

        return "pages/admin/users/users-edit"; // File HTML baru
    }

    // ===========================
    // UPDATE METHOD UPDATE (EDIT)
    // ===========================

    // 2. PROSES UPDATE (POST)
    @PostMapping("/users/update")
    @ResponseBody
    // public String updateUser(@ModelAttribute UserDto userDto, RedirectAttributes
    // attributes) {
    // public ResponseEntity<?> updateUser(@ModelAttribute UserDto userDto) {
    public ResponseEntity<?> updateUser(@ModelAttribute UserDto userDto,
            @RequestParam("roles") List<String> roles,
            RedirectAttributes redirectAttributes) {
        try {

            // // 1. Validasi Duplikat (EDIT - Kecuali diri sendiri)
            // if (userRepository.existsByUsernameAndIdNot(userDto.getUsername(),
            // userDto.getId())) {
            // return ResponseEntity.badRequest()
            // .body("{\"status\": \"error\", \"message\": \"Username sudah digunakan oleh
            // pengguna lain!\"}");
            // }
            // if (userRepository.existsByEmailAndIdNot(userDto.getEmail(),
            // userDto.getId())) {
            // return ResponseEntity.badRequest()
            // .body("{\"status\": \"error\", \"message\": \"Email sudah digunakan oleh
            // pengguna lain!\"}");
            // }

            if (userRepository.existsByUsernameAndIdNot(userDto.getUsername(), userDto.getId())) {
                return ResponseEntity.badRequest().body(
                        "{\"status\": \"error\", \"field\": \"username\", \"message\": \"Username sudah terdaftar\"}");
            }
            // Cek Duplikat Email (Kecuali diri sendiri)
            if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), userDto.getId())) {
                return ResponseEntity.badRequest()
                        .body("{\"status\": \"error\", \"field\": \"email\", \"message\": \"Email sudah terdaftar\"}");
            }
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

            // 1. NIK: Bersihkan titik lagi sebelum simpan update
            if (userDto.getNik() != null) {
                user.setNik(userDto.getNik().replaceAll("[^0-9]", ""));
            }

            // 2. NO TELP: Tambahkan '0' lagi
            if (userDto.getPhoneNumber() != null) {
                String rawPhone = userDto.getPhoneNumber().replaceAll("[^0-9]", "");
                if (rawPhone.startsWith("0"))
                    rawPhone = rawPhone.substring(1);
                user.setPhoneNumber("0" + rawPhone);
            }

            // E. UPDATE DATA PRIBADI & LAINNYA
            // (Kita update juga agar sinkron dengan form edit yang mungkin diubah user)
            user.setFullName(userDto.getFullName());
            user.setBirthPlace(userDto.getBirthPlace());
            user.setBirthDate(userDto.getBirthDate());
            user.setGender(userDto.getGender());
            // user.setNik(userDto.getNik());
            // user.setPhoneNumber(userDto.getPhoneNumber());
            user.setAddress(userDto.getAddress());
            user.setPostalCode(userDto.getPostalCode());
            user.setCitizenship(userDto.getCitizenship());
            // user.setNoMet(userDto.getNoMet());
            // 3. NO MET (Di dalam loop role Asesor)
            // user.setNoMet("MET." + userDto.getNoMet());
            if (roles.contains("Asesor")) {
                if (userDto.getNoMet() != null) {
                    user.setNoMet("MET." + userDto.getNoMet());
                }
            } else {
                // Jika bukan Asesor, bersihkan No MET
                user.setNoMet(null);
            }

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
            // user.setSubDistrictId(userDto.getSubDistrictId());

            // Signature: Hanya update jika ada tanda tangan baru, jika kosong biarkan yang
            // lama
            if (userDto.getSignatureBase64() != null && !userDto.getSignatureBase64().isEmpty()) {
                user.setSignatureBase64(userDto.getSignatureBase64());
            }

            // F. SIMPAN PERUBAHAN
            userRepository.save(user);

            // attributes.addFlashAttribute("success", "Data pengguna berhasil
            // diperbarui!");

            // } catch (Exception e) {
            // e.printStackTrace();
            // attributes.addFlashAttribute("error", "Gagal mengupdate data: " +
            // e.getMessage());
            // return ResponseEntity.ok()
            // .body("{\"status\": \"success\", \"message\": \"Data pengguna berhasil
            // diperbarui!\"}");

            // } catch (Exception e) {
            // return ResponseEntity.badRequest()
            // .body("{\"status\": \"error\", \"message\": \"Gagal update: " +
            // e.getMessage() + "\"}");

            // // return "redirect:/admin/users/users-edit/" + userDto.getId(); // Kembali
            // ke
            // // form edit jika gagal
            // }

            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"Data berhasil diperbarui\", \"id\": " + user.getId()
                            + "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal: " + e.getMessage() + "\"}");
        }

    }

    // @GetMapping("/data-pengguna/delete/{id}")
    // public String deletePengguna(@PathVariable Long id, RedirectAttributes
    // attributes) {

    // try {
    // // Cek apakah user ada sebelum dihapus (Opsional, tapi bagus)
    // if (!userRepository.existsById(id)) {
    // attributes.addFlashAttribute("error", "Data pengguna tidak ditemukan!");
    // return "redirect:/admin/data-pengguna";
    // }

    // // Lakukan penghapusan data
    // userRepository.deleteById(id);

    // // Kirim pesan sukses
    // attributes.addFlashAttribute("success", "Data pengguna berhasil dihapus
    // secara permanen!");
    // } catch (Exception e) {
    // // Tangani error, misal Foreign Key Constraint
    // attributes.addFlashAttribute("error",
    // "Gagal menghapus pengguna. Pastikan data terkait (misal: data registrasi)
    // sudah dihapus.");
    // }
    // return "redirect:/admin/data-pengguna";
    // }

    // FITUR BARU: HAPUS ROLE SPESIFIK
    @GetMapping("/data-pengguna/delete-role")
    public String deleteUserRole(@RequestParam("userId") Long userId,
            @RequestParam("roleName") String roleName,
            RedirectAttributes attributes) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // 1. Cari Role yang mau dihapus
            Role roleToRemove = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role tidak ditemukan"));

            // 2. Hapus Role dari Set roles user
            if (user.getRoles().contains(roleToRemove)) {
                user.getRoles().remove(roleToRemove);
                // ============================================================
                // FITUR BARU: PEMBERSIHAN DATA (DATA CLEANUP)
                // ============================================================
                // Jika Role yang dihapus adalah ASESOR, hapus data spesifik Asesor
                if (roleName.equalsIgnoreCase("Asesor")) {
                    user.setNoMet(null); // Hapus No MET
                    user.setJobTypeId(null); // Hapus Jenis Pekerjaan (Relasi)
                }
                // Jika Role yang dihapus adalah ASESI, hapus data spesifik Asesi
                else if (roleName.equalsIgnoreCase("Asesi")) {
                    user.setCitizenship(null); // Hapus Kewarganegaraan
                    user.setCompanyName(null);
                    user.setPosition(null);
                    user.setOfficePhone(null);
                    user.setOfficeEmail(null);
                    user.setOfficeFax(null);
                    user.setOfficeAddress(null);
                    // Data umum (Tgl Lahir, NIK, dll) biasanya tetap dipertahankan
                    // karena melekat pada orangnya, bukan hanya rolenya.
                    // Tapi jika ingin dihapus juga, tambahkan disini:
                    // user.setNik(null);
                }
                // user.setFullName(user.getFullName());
                // user.setBirthPlace(null);
                // user.setBirthDate(null);
                // user.setGender(null);
                // user.setNik(null);
                // user.setPhoneNumber(null);
                // user.setAddress(null);
                // user.setPostalCode(null);
                // user.setCitizenship(null);
                // user.setNoMet(null);

            }

            // 3. Cek sisa role
            if (user.getRoles().isEmpty()) {
                // Jika tidak punya role lagi, hapus user permanen
                userRepository.delete(user);
                attributes.addFlashAttribute("success", "Akun pengguna dihapus karena tidak memiliki role lagi.");
            } else {
                // Jika masih punya role lain, simpan perubahannya saja
                userRepository.save(user);
                attributes.addFlashAttribute("success", "Role " + roleName + " berhasil dicabut dari pengguna.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Gagal menghapus role: " + e.getMessage());
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