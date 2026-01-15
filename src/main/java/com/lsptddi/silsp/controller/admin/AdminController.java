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
import com.lsptddi.silsp.dto.SuratTugasDto;
import com.lsptddi.silsp.dto.TukDto;
import com.lsptddi.silsp.dto.UserDto;
import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Pastikan ada ini
import com.lsptddi.silsp.repository.TypeEducationRepository;
import com.lsptddi.silsp.repository.TypePekerjaanRepository;
import com.lsptddi.silsp.repository.TypePengajuanSkemaRepository;
import com.lsptddi.silsp.repository.TypeSkemaRepository;
import com.lsptddi.silsp.repository.TypeTukRepository;
import com.lsptddi.silsp.repository.ScheduleRepository;
import com.lsptddi.silsp.repository.TypeSumberAnggaranRepository;
import com.lsptddi.silsp.repository.TypePemberiAnggaranRepository;
import com.lsptddi.silsp.repository.RoleRepository;
import com.lsptddi.silsp.repository.SkemaRepository;
import com.lsptddi.silsp.repository.TukRepository;
import com.lsptddi.silsp.service.SuratTugasService;
import com.lsptddi.silsp.repository.SuratTugasRepository;
import com.lsptddi.silsp.repository.UnitSkemaRepository;
import com.lsptddi.silsp.repository.PersyaratanSkemaRepository;
import com.lsptddi.silsp.repository.UnitElemenSkemaRepository;
import com.lsptddi.silsp.repository.KukSkemaRepository;
import com.lsptddi.silsp.repository.UserRepository;
import com.lsptddi.silsp.service.TukService;
import com.lsptddi.silsp.service.ScheduleService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.lsptddi.silsp.dto.UserRoleDto;
import com.lsptddi.silsp.dto.AsesorListDto;
import com.lsptddi.silsp.dto.ScheduleDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.lsptddi.silsp.service.PdfService;
// import com.lsptddi.silsp.dto.SuratTugasDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Sort;
// import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

// import java.util.HashMap;

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
    @Autowired
    private ScheduleRepository scheduleRepository;

    private final String UPLOAD_DIR = "uploads/skema/"; // Pastikan folder ini ada

    @Autowired
    private TukRepository tukRepository;
    @Autowired
    private TypeTukRepository tukTypeRepository;

    @Autowired
    private SuratTugasService suratTugasService;
    @Autowired
    private SuratTugasRepository suratTugasRepository;

    @Autowired
    private TypeSumberAnggaranRepository typeSumberAnggaranRepository;
    @Autowired
    private TypePemberiAnggaranRepository typePemberiAnggaranRepository;

    @Autowired
    private UnitSkemaRepository unitSkemaRepository;

    @Autowired
    private PersyaratanSkemaRepository persyaratanSkemaRepository;

    @Autowired
    private UnitElemenSkemaRepository unitElemenSkemaRepository;

    @Autowired
    private KukSkemaRepository kukSkemaRepository;

    @Autowired
    private TukService tukService;

    @Autowired
    private PdfService pdfService; // Inject Service PDF

    @Autowired
    private ScheduleService scheduleService;

    // Di dalam class AsesiController

    @GetMapping("/dashboard")
    public String index(Model model) {

        return "pages/admin/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Mapping ke DTO untuk ditampilkan di form
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        // dto.setNoTelp(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setBirthPlace(user.getBirthPlace());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setNik(user.getNik());
        dto.setPostalCode(user.getPostalCode());

        // 2. NO TELP: Hapus '0' di depan agar di form jadi (811...)
        if (user.getPhoneNumber() != null && user.getPhoneNumber().startsWith("0")) {
            dto.setPhoneNumber(user.getPhoneNumber().substring(1));
        } else {
            dto.setPhoneNumber(user.getPhoneNumber());
        }
        // dto.setAvatar(user.getAvatar()); // Removed: setAvatar expects MultipartFile,
        // not String
        dto.setSignatureBase64(user.getSignatureBase64());

        dto.setProvinceId(user.getProvinceId());
        dto.setCityId(user.getCityId());
        dto.setDistrictId(user.getDistrictId());

        model.addAttribute("userDto", dto);
        return "pages/admin/admin-profile"; // Mengarah ke file HTML shared
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

            // // D. Mapping Unit Skema (Looping Array)
            // if (dto.getKodeUnit() != null) {
            // for (int i = 0; i < dto.getKodeUnit().size(); i++) {
            // UnitSkema unit = new UnitSkema();
            // unit.setCode(dto.getKodeUnit().get(i));
            // unit.setTitle(dto.getJudulUnit().get(i));
            // schema.addUnit(unit); // Helper method handles relationship
            // }
            // }

            // E. Mapping Persyaratan (Looping Array)
            // if (dto.getPersyaratan() != null) {
            // for (String reqText : dto.getPersyaratan()) {
            // if (reqText != null && !reqText.trim().isEmpty()) {
            // PersyaratanSkema req = new PersyaratanSkema();
            // req.setDescription(reqText);
            // schema.addRequirement(req);
            // }
            // }
            // }

            // Simpan Parent (Cascade akan menyimpan Unit & Req)
            schemaRepository.save(schema);

            // ============================================================
            // PROSES DATA BERTINGKAT (Unit -> Elemen -> KUK)
            // ============================================================

            // List Penampung untuk Referensi ID
            List<UnitSkema> savedUnits = new ArrayList<>();
            List<UnitElemenSkema> savedElements = new ArrayList<>();

            // A. Simpan Unit
            if (dto.getUnits() != null) {
                for (SkemaDto.UnitItem item : dto.getUnits()) {
                    UnitSkema unit = new UnitSkema();
                    unit.setSkema(schema); // Link ke Skema
                    unit.setCode(item.getKodeUnit());
                    unit.setTitle(item.getJudulUnit());

                    // Simpan & Masukkan ke List Penampung (Index 0, 1, 2...)
                    savedUnits.add(unitSkemaRepository.save(unit));
                }
            }

            // B. Simpan Elemen (Link ke Unit via Index)
            if (dto.getElements() != null) {
                for (SkemaDto.ElementItem item : dto.getElements()) {
                    // Validasi: Pastikan Index Unit Valid
                    if (item.getUnitIndex() != null && item.getUnitIndex() < savedUnits.size()) {
                        UnitElemenSkema el = new UnitElemenSkema();
                        el.setNoElemen(item.getNoElemen());
                        el.setNamaElemen(item.getNamaElemen());

                        // Link ke Unit yang TEPAT (menggunakan index dari form)
                        el.setSkemaUnit(savedUnits.get(item.getUnitIndex()));

                        // Simpan & Masukkan ke List Penampung
                        savedElements.add(unitElemenSkemaRepository.save(el));
                    }
                }
            }

            // C. Simpan KUK (Link ke Elemen via Index)
            if (dto.getKuks() != null) {
                for (SkemaDto.KukItem item : dto.getKuks()) {
                    // Validasi: Pastikan Index Elemen Valid
                    if (item.getElementIndex() != null && item.getElementIndex() < savedElements.size()) {
                        KukSkema kuk = new KukSkema();
                        kuk.setNamaKuk(item.getNamaKuk());

                        // Link ke Elemen yang TEPAT
                        kuk.setSchemaElement(savedElements.get(item.getElementIndex()));

                        kukSkemaRepository.save(kuk);
                    }
                }
            }

            // D. Simpan Persyaratan (Logic Lama)
            if (dto.getPersyaratan() != null) {
                for (String reqText : dto.getPersyaratan()) {
                    if (reqText != null && !reqText.trim().isEmpty()) {
                        PersyaratanSkema req = new PersyaratanSkema();
                        req.setDescription(reqText);
                        req.setSkema(schema); // Link ke Skema
                        persyaratanSkemaRepository.save(req);
                    }
                }
            }

            return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Skema berhasil disimpan!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal menyimpan: " + e.getMessage() + "\"}");
        }
    }

    // @PostMapping("/skema/save")
    // @ResponseBody
    // public ResponseEntity<?> saveSchema(@ModelAttribute SkemaDto dto) {
    // try {
    // // 1. Simpan Parent (Skema)
    // Skema schema = new Skema();
    // schema.setName(dto.getNamaSkema());
    // schema.setCode(dto.getKodeSkema());
    // schema.setLevel(dto.getLevel());
    // schema.setNoSkkni(dto.getNoSkkni());
    // schema.setSkkniYear(dto.getTahunSkkni());
    // schema.setEstablishmentDate(dto.getTanggalPenetapan());

    // // Set Relasi Type & Mode
    // if (dto.getJenisSkemaId() != null)
    // schema.setSchemaType(schemaTypeRepository.findById(dto.getJenisSkemaId()).orElse(null));
    // if (dto.getModeSkemaId() != null)
    // schema.setSchemaMode(schemaModeRepository.findById(dto.getModeSkemaId()).orElse(null));

    // // Upload File
    // if (dto.getFileSkema() != null && !dto.getFileSkema().isEmpty()) {
    // String fileName = saveFile(dto.getFileSkema());
    // schema.setDocumentPath(fileName);
    // }

    // // Simpan Skema Dulu (Agar dapat ID)
    // schema = schemaRepository.save(schema);

    // return ResponseEntity.ok().body("{\"status\": \"success\", \"message\":
    // \"Skema lengkap berhasil disimpan!\"}");

    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\":
    // \"Gagal: " + e.getMessage() + "\"}");
    // }
    // }

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

            // // D. Update Unit Skema (Strategi: Clear & Add All)
            // schema.getUnits().clear(); // Hapus unit lama
            // if (dto.getKodeUnit() != null) {
            // for (int i = 0; i < dto.getKodeUnit().size(); i++) {
            // UnitSkema unit = new UnitSkema();
            // unit.setCode(dto.getKodeUnit().get(i));
            // unit.setTitle(dto.getJudulUnit().get(i));
            // schema.addUnit(unit); // Tambahkan yang baru
            // }
            // }

            // // E. Update Persyaratan (Strategi: Clear & Add All)
            // schema.getRequirements().clear(); // Hapus req lama
            // if (dto.getPersyaratan() != null) {
            // for (String reqText : dto.getPersyaratan()) {
            // if (reqText != null && !reqText.trim().isEmpty()) {
            // PersyaratanSkema req = new PersyaratanSkema();
            // req.setDescription(reqText);
            // schema.addRequirement(req);
            // }
            // }
            // }

            schemaRepository.save(schema);

            // ============================================================
            // PROSES DATA BERTINGKAT (Unit -> Elemen -> KUK)
            // ============================================================

            // List Penampung untuk Referensi ID
            List<UnitSkema> savedUnits = new ArrayList<>();
            List<UnitElemenSkema> savedElements = new ArrayList<>();

            // A. Simpan Unit
            if (dto.getUnits() != null) {
                for (SkemaDto.UnitItem item : dto.getUnits()) {
                    UnitSkema unit = new UnitSkema();
                    unit.setSkema(schema); // Link ke Skema
                    unit.setCode(item.getKodeUnit());
                    unit.setTitle(item.getJudulUnit());

                    // Simpan & Masukkan ke List Penampung (Index 0, 1, 2...)
                    savedUnits.add(unitSkemaRepository.save(unit));
                }
            }

            // B. Simpan Elemen (Link ke Unit via Index)
            if (dto.getElements() != null) {
                for (SkemaDto.ElementItem item : dto.getElements()) {
                    // Validasi: Pastikan Index Unit Valid
                    if (item.getUnitIndex() != null && item.getUnitIndex() < savedUnits.size()) {
                        UnitElemenSkema el = new UnitElemenSkema();
                        el.setNoElemen(item.getNoElemen());
                        el.setNamaElemen(item.getNamaElemen());

                        // Link ke Unit yang TEPAT (menggunakan index dari form)
                        el.setSkemaUnit(savedUnits.get(item.getUnitIndex()));

                        // Simpan & Masukkan ke List Penampung
                        savedElements.add(unitElemenSkemaRepository.save(el));
                    }
                }
            }

            // C. Simpan KUK (Link ke Elemen via Index)
            if (dto.getKuks() != null) {
                for (SkemaDto.KukItem item : dto.getKuks()) {
                    // Validasi: Pastikan Index Elemen Valid
                    if (item.getElementIndex() != null && item.getElementIndex() < savedElements.size()) {
                        KukSkema kuk = new KukSkema();
                        kuk.setNamaKuk(item.getNamaKuk());

                        // Link ke Elemen yang TEPAT
                        kuk.setSchemaElement(savedElements.get(item.getElementIndex()));

                        kukSkemaRepository.save(kuk);
                    }
                }
            }

            // D. Simpan Persyaratan (Logic Lama)
            if (dto.getPersyaratan() != null) {
                for (String reqText : dto.getPersyaratan()) {
                    if (reqText != null && !reqText.trim().isEmpty()) {
                        PersyaratanSkema req = new PersyaratanSkema();
                        req.setDescription(reqText);
                        req.setSkema(schema); // Link ke Skema
                        persyaratanSkemaRepository.save(req);
                    }
                }
            }

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
        return "redirect:/admin/tuk";
    }

    @GetMapping("/jadwal-sertifikasi")

    public String showScheduleList(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Schedule> pageSchedule = scheduleRepository.searchSchedule(keyword, pageable);

        if (keyword != null && !keyword.isEmpty()) {
            // PERBAIKAN: Panggil method yang sudah kita perbaiki di Repo
            pageSchedule = scheduleRepository.searchSchedule(keyword, pageable);
        } else {
            pageSchedule = scheduleRepository.findAll(pageable);
        }

        model.addAttribute("listJadwal", pageSchedule.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSchedule.getTotalPages());
        model.addAttribute("totalItems", pageSchedule.getTotalElements());
        model.addAttribute("size", size);

        return "pages/admin/jadwal/sertifikasi-list";
    }

    @GetMapping("/jadwal-sertifikasi/detail")
    public String showDetailJadwalAsesmen(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/jadwal/sertifikasi-detail";
    }

    // HALAMAN TAMBAH JADWAL
    @GetMapping("/jadwal-sertifikasi/jadwal-tambah")
    public String showAddSchedule(Model model) {
        // Ambil data pendukung
        model.addAttribute("listTuk", tukRepository.findAll());
        // Ambil User yang role-nya Asesor (Buat query khusus di UserRepository jika
        // perlu, atau filter disini)
        List<User> asesorList = userRepository.findByRolesName("Asesor");
        model.addAttribute("listAsesor", asesorList);
        model.addAttribute("listSkema", schemaRepository.findAll());
        model.addAttribute("listSumberAnggaran", typeSumberAnggaranRepository.findAll());
        model.addAttribute("listPemberiAnggaran", typePemberiAnggaranRepository.findAll());

        // Generate kode bayangan untuk tampilan
        String nextCode = scheduleService.generateScheduleCode();

        ScheduleDto dto = new ScheduleDto();
        dto.setCode(nextCode); // Pre-fill code
        model.addAttribute("scheduleDto", dto);

        return "pages/admin/jadwal/sertifikasi-add";
    }

    // PROSES SIMPAN

    @PostMapping("/jadwal-sertifikasi/save")
    @ResponseBody // Return JSON
    public ResponseEntity<?> saveSchedule(@ModelAttribute ScheduleDto dto) {
        try {
            scheduleService.saveSchedule(dto);
            // Kirim sinyal sukses ke JS
            return ResponseEntity.ok().body("{\"status\":\"success\", \"type\":\"add\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }

    }

    @GetMapping("/jadwal-sertifikasi/view/{id}")
    public String showViewSchedule(@PathVariable Long id, Model model) {
        // Ambil data jadwal beserta relasinya (JPA otomatis fetch data relasi)
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));

        // Kirim entity langsung ke View (Read-Only)
        model.addAttribute("schedule", schedule);

        return "pages/admin/jadwal/sertifikasi-view";
    }

    @GetMapping("/jadwal-sertifikasi/edit/{id}")
    public String showEditSchedule(@PathVariable Long id, Model model) {
        // 1. Ambil Data Jadwal dari DB
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));

        // 2. Mapping Entity ke DTO
        ScheduleDto dto = new ScheduleDto();
        dto.setId(schedule.getId());
        dto.setName(schedule.getName());
        dto.setCode(schedule.getCode());
        dto.setBnspCode(schedule.getBnspCode());
        dto.setStartDate(schedule.getStartDate());
        dto.setQuota(schedule.getQuota());

        // Mapping Relasi TUK & Anggaran
        if (schedule.getTuk() != null)
            dto.setTukId(schedule.getTuk().getId());
        if (schedule.getBudgetSource() != null)
            dto.setBudgetSource(schedule.getBudgetSource().getId());
        if (schedule.getBudgetProvider() != null)
            dto.setBudgetProvider(schedule.getBudgetProvider().getId());

        // 3. Mapping Relasi List (PENTING UNTUK EDIT)
        // Kita ambil ID saja untuk DTO, tapi kita kirim object lengkap ke Model untuk
        // ditampilkan di Tabel
        dto.setAssessorIds(schedule.getAssessors().stream()
                .map(sa -> sa.getAsesor().getId())
                .collect(Collectors.toList()));

        dto.setSchemaIds(schedule.getSchemas().stream()
                .map(ss -> ss.getSchema().getId())
                .collect(Collectors.toList()));

        // 4. Kirim Data ke View
        model.addAttribute("scheduleDto", dto);

        // Kirim Object List Asli untuk di-render di tabel HTML (biar bisa ambil Nama/No
        // MET)
        model.addAttribute("existingAssessors", schedule.getAssessors());
        model.addAttribute("existingSchemas", schedule.getSchemas());

        // Data Referensi Dropdown
        model.addAttribute("listTuk", tukRepository.findAll());
        List<User> asesorList = userRepository.findByRolesName("Asesor");
        model.addAttribute("listAsesor", asesorList);
        // model.addAttribute("listAsesor",
        // roleRepository.findByName("Asesor").get().getUsers());
        model.addAttribute("listSkema", schemaRepository.findAll());
        model.addAttribute("listSumberAnggaran", typeSumberAnggaranRepository.findAll());
        model.addAttribute("listPemberiAnggaran", typePemberiAnggaranRepository.findAll());

        return "pages/admin/jadwal/sertifikasi-edit";
    }

    // ==========================================
    // 2. PROSES UPDATE JADWAL (POST)
    // ==========================================
    @PostMapping("/jadwal-sertifikasi/update")
    @ResponseBody // Return JSON
    public ResponseEntity<?> updateSchedule(@ModelAttribute ScheduleDto dto) {
        try {
            scheduleService.updateSchedule(dto);
            // Kirim sinyal sukses ke JS
            return ResponseEntity.ok().body("{\"status\":\"success\", \"type\":\"edit\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/jadwal-sertifikasi/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleRepository.deleteById(id); // Cascade akan menghapus relasi di tabel asesor & skema otomatis
            redirectAttributes.addFlashAttribute("success", "Jadwal berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus jadwal.");
        }
        return "redirect:/admin/jadwal-sertifikasi";
    }

    // @GetMapping("/surat-tugas-asesor")
    // public String showDataSuratTugas(Model model) { // 1. Tambahkan Model sebagai
    // parameter

    // return "pages/admin/surat/surat-tugas-list";
    // }

    @GetMapping("/surat-tugas-asesor")
    public String showSuratTugasList(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword) {

        // Setup Pagination (Urutkan ID Descending agar surat terbaru di atas)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<SuratTugas> pageSurat;

        if (keyword != null && !keyword.isEmpty()) {
            // Pencarian berdasarkan Nomor Surat atau Nama Asesor
            pageSurat = suratTugasRepository.searchSuratTugas(keyword, pageable);
        } else {
            pageSurat = suratTugasRepository.findAll(pageable);
        }

        model.addAttribute("listSurat", pageSurat.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSurat.getTotalPages());
        model.addAttribute("totalItems", pageSurat.getTotalElements());
        model.addAttribute("size", size);

        return "pages/admin/surat/surat-tugas-list";
    }

    // @GetMapping("/surat-tugas-asesor/buat")
    // public String showFormSuratTugas(Model model) {
    // SuratTugasDto dto = new SuratTugasDto();

    // // A. Auto Numbering
    // dto.setNomorSurat(suratTugasService.generateNomorSurat());

    // // B. Auto Date (Hari Ini)
    // dto.setTanggalSurat(LocalDate.now());

    // model.addAttribute("suratDto", dto);

    // // Hanya kirim list asesor, sisanya (jadwal, skema, tuk) diambil via API JS
    // List<User> asesorList = userRepository.findByRolesName("Asesor");
    // model.addAttribute("listAsesor", asesorList);

    // // Ambil list Jadwal (Yang berisi TUK & Skema)
    // model.addAttribute("listJadwal",
    // scheduleRepository.findAll(Sort.by("id").descending()));

    // return "pages/admin/surat/surat-tugas-add";
    // }

    // 1. HALAMAN FORM SURAT TUGAS (GET) - MODIFIED
    @GetMapping("/surat-tugas-asesor/buat")
    public String showFormSuratTugas(Model model) {
        SuratTugasDto dto = new SuratTugasDto();

        // Pre-fill Nomor & Tanggal (Otomatis Hari Ini & Nomor Urut Baru)
        dto.setNomorSurat(suratTugasService.generateNomorSurat());
        dto.setTanggalSurat(LocalDate.now());

        model.addAttribute("suratDto", dto);

        // --- PERUBAHAN DISINI: LOAD SEMUA DATA SECARA INDEPENDEN ---

        // 1. Load Semua Jadwal
        model.addAttribute("listJadwal", scheduleRepository.findAll(Sort.by("id").descending()));

        // // 2. Load Semua Asesor (Tanpa filter jadwal)
        // List<User> asesorList = userRepository.findByRolesName("Asesor");
        // model.addAttribute("listAsesor", asesorList);

        // 3. Load Semua Skema (Tanpa filter jadwal)
        // model.addAttribute("listSkema", schemaRepository.findAll());

        return "pages/admin/surat/surat-tugas-add";
    }

    @GetMapping("/api/check-surat-tugas")
    @ResponseBody
    public ResponseEntity<?> checkSuratTugasExisting(@RequestParam Long jadwalId, @RequestParam Long asesorId) {
        Schedule jadwal = scheduleRepository.findById(jadwalId).orElse(null);
        User asesor = userRepository.findById(asesorId).orElse(null);

        if (jadwal != null && asesor != null) {
            // Cek di Repository apakah sudah ada
            boolean exists = suratTugasRepository.existsByJadwalAndAsesor(jadwal, asesor);
            return ResponseEntity.ok(exists); // Return true jika sudah ada
        }
        return ResponseEntity.ok(false);
    }

    // 2. API INTERNAL: Get Detail Jadwal (Asesor & Skema)
    // Dipanggil via AJAX saat user memilih Jadwal
    // @GetMapping("/api/internal/jadwal/{id}/details")
    // @ResponseBody
    // public ResponseEntity<?> getJadwalDetails(@PathVariable Long id) {
    // Schedule schedule = scheduleRepository.findById(id).orElse(null);
    // if (schedule == null)
    // return ResponseEntity.notFound().build();

    // Map<String, Object> response = new HashMap<>();

    // // HANYA AMBIL SKEMA (Karena Asesor bebas pilih)
    // List<Map<String, Object>> schemas = schedule.getSchemas().stream().map(ss ->
    // {
    // Map<String, Object> map = new HashMap<>();
    // map.put("id", ss.getSchema().getId());
    // map.put("name", ss.getSchema().getName());
    // return map;
    // }).collect(Collectors.toList());

    // response.put("schemas", schemas);

    // return ResponseEntity.ok(response);
    // }

    @GetMapping("/api/internal/jadwal/{id}/details")
    @ResponseBody
    public ResponseEntity<?> getJadwalDetails(@PathVariable Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null)
            return ResponseEntity.notFound().build();

        // Siapkan Data JSON custom
        Map<String, Object> response = new HashMap<>();

        // List Asesor di Jadwal tersebut
        List<Map<String, Object>> asesors = schedule.getAssessors().stream().map(sa -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sa.getAsesor().getId());
            map.put("name", sa.getAsesor().getFullName());
            map.put("noMet", sa.getAsesor().getNoMet());
            return map;
        }).collect(Collectors.toList());

        // List Skema di Jadwal tersebut
        List<Map<String, Object>> schemas = schedule.getSchemas().stream().map(ss -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ss.getSchema().getId());
            map.put("name", ss.getSchema().getName());
            return map;
        }).collect(Collectors.toList());

        response.put("asesors", asesors);
        response.put("schemas", schemas);

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------
    // 3. GENERATE PDF & SIMPAN (POST)
    // -------------------------------------------------------------
    // @PostMapping("/surat-tugas-asesor/generate")
    // public ResponseEntity<?> generateSuratTugas(@ModelAttribute SuratTugasDto
    // dto) {
    // try {
    // User asesor = userRepository.findById(dto.getAsesorId()).orElseThrow();
    // Schedule jadwal =
    // scheduleRepository.findById(dto.getJadwalId()).orElseThrow();

    // // VALIDASI BACKEND: Cegah double insert jika user menekan tombol 2x
    // if (suratTugasRepository.existsByJadwalAndAsesor(jadwal, asesor)) {
    // return ResponseEntity.badRequest()
    // .body("Surat tugas untuk asesor ini pada jadwal tersebut sudah
    // dibuat!".getBytes());
    // }

    // // A. Simpan Database
    // SuratTugas surat = new SuratTugas();
    // surat.setNomorSurat(dto.getNomorSurat());
    // surat.setTanggalSurat(dto.getTanggalSurat());
    // surat.setBulanRomawi(suratTugasService.getRomanMonth(dto.getTanggalSurat().getMonthValue()));
    // surat.setTahun(dto.getTanggalSurat().getYear());
    // surat.setAsesor(asesor);
    // surat.setJadwal(jadwal);
    // suratTugasRepository.save(surat);

    // // B. Generate PDF (Logic Sama)
    // Map<String, Object> data = new HashMap<>();
    // DateTimeFormatter indoFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy",
    // new Locale("id", "ID"));

    // data.put("nomorSurat", dto.getNomorSurat());
    // data.put("tanggalSurat", dto.getTanggalSurat().format(indoFormat));
    // data.put("namaAsesor", asesor.getFullName());

    // String genderDb = asesor.getGender() != null ? asesor.getGender().trim() :
    // "";
    // String sapaan = (genderDb.equalsIgnoreCase("Laki-laki") ||
    // genderDb.equalsIgnoreCase("L")) ? "Bapak"
    // : "Ibu";
    // data.put("genderSapaan", sapaan);

    // data.put("noMet", asesor.getNoMet() != null ? asesor.getNoMet() : "-");

    // String namaSkema = jadwal.getSchemas().isEmpty() ? "-" :
    // jadwal.getSchemas().get(0).getSchema().getName();
    // data.put("namaSkema", namaSkema);
    // data.put("tanggalAsesmen", jadwal.getStartDate().format(indoFormat));
    // data.put("namaTuk", jadwal.getTuk().getName());

    // byte[] pdfBytes =
    // pdfService.generatePdf("pages/admin/surat/surat-tugas-template", data);
    // String filename = "ST_" + asesor.getFullName().replace(" ", "_") + ".pdf";

    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
    // .contentType(MediaType.APPLICATION_PDF)
    // .body(pdfBytes);

    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.internalServerError().build();
    // }
    // }

    // @PostMapping("/surat-tugas-asesor/generate")
    // public ResponseEntity<?> generateSuratTugas(@ModelAttribute SuratTugasDto
    // dto) {
    // try {
    // // A. Ambil Data Referensi
    // Schedule jadwal =
    // scheduleRepository.findById(dto.getJadwalId()).orElseThrow();
    // User asesor = userRepository.findById(dto.getAsesorId()).orElseThrow();
    // Skema skema = schemaRepository.findById(dto.getSkemaId()).orElseThrow();

    // // B. Simpan ke Database (Sekaligus Generate Nomor)
    // // Service akan melempar error jika duplikat
    // SuratTugas suratBaru = suratTugasService.createSuratTugas(jadwal, asesor,
    // skema);

    // // C. Siapkan Data PDF
    // Map<String, Object> data = new HashMap<>();
    // DateTimeFormatter indoFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy",
    // new Locale("id", "ID"));

    // data.put("nomorSurat", suratBaru.getNomorSurat());
    // data.put("tanggalSurat", suratBaru.getTanggalSurat().format(indoFormat));

    // data.put("namaAsesor", asesor.getFullName());
    // data.put("genderSapaan", "L".equalsIgnoreCase(asesor.getGender()) ? "Bapak" :
    // "Ibu");
    // data.put("noMet", asesor.getNoMet() != null ? asesor.getNoMet() : "-");

    // data.put("namaSkema", skema.getName());
    // data.put("tanggalAsesmen", jadwal.getStartDate().format(indoFormat));
    // data.put("namaTuk", jadwal.getTuk().getName());

    // // D. Generate File PDF
    // byte[] pdfBytes =
    // pdfService.generatePdf("pages/admin/surat/surat-tugas-template", data);
    // String filename = "ST_" + suratBaru.getNomorSurat().replace("/", "_") +
    // ".pdf";

    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
    // .contentType(MediaType.APPLICATION_PDF)
    // .body(pdfBytes);

    // } catch (RuntimeException e) {
    // // Tangkap Error Duplikat/Lainnya dan kirim ke Frontend (sebagai HTML Error
    // Page
    // // sementara)
    // // Idealnya return JSON error dan dihandle AJAX, tapi karena ini form submit
    // PDF
    // // download:
    // // Kita return Text plain error agar user tahu
    // return ResponseEntity.badRequest().body(e.getMessage().getBytes());
    // }
    // }

    @PostMapping("/surat-tugas-asesor/generate")
    public ResponseEntity<?> generateSuratTugas(@ModelAttribute SuratTugasDto dto) {
        try {
            // A. Ambil Data
            Schedule jadwal = scheduleRepository.findById(dto.getJadwalId()).orElseThrow();
            User asesor = userRepository.findById(dto.getAsesorId()).orElseThrow();
            Skema skema = schemaRepository.findById(dto.getSkemaId()).orElseThrow();

            // B. Simpan Data ke DB (Record Baru)
            // Note: Service createSuratTugas Anda sebelumnya mungkin sudah menghandle
            // duplicate check,
            // tapi kita double check di sini atau biarkan service melempar error.
            SuratTugas suratBaru = suratTugasService.createSuratTugas(jadwal, asesor, skema);

            // C. Siapkan Data PDF
            Map<String, Object> data = new HashMap<>();
            DateTimeFormatter indoFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

            data.put("nomorSurat", suratBaru.getNomorSurat());
            data.put("tanggalSurat", suratBaru.getTanggalSurat().format(indoFormat));
            data.put("namaAsesor", asesor.getFullName());
            data.put("genderSapaan", "L".equalsIgnoreCase(asesor.getGender()) ? "Bapak" : "Ibu");
            data.put("noMet", asesor.getNoMet() != null ? asesor.getNoMet() : "-");
            data.put("namaSkema", skema.getName());
            data.put("tanggalAsesmen", jadwal.getStartDate().format(indoFormat));
            data.put("namaTuk", jadwal.getTuk().getName());

            // D. Generate Bytes PDF
            byte[] pdfBytes = pdfService.generatePdf("pages/admin/surat/surat-tugas-template", data);

            // --- E. SIMPAN FILE KE SERVER (FITUR BARU) ---
            // String filename = "ST_" +
            // suratBaru.getNomorSurat().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
            String filename = suratBaru.getNomorSurat().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

            String uploadDir = "uploads/surat_tugas/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, pdfBytes); // Simpan fisik file

            // F. Update Database dengan Path File
            suratBaru.setFilePath("/" + uploadDir + filename);
            suratTugasRepository.save(suratBaru);

            // G. Return Download Stream ke Browser
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(("Gagal: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/surat-tugas-asesor/view")
    public String showViewSuratTugas(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/surat/surat-tugas-view";
    }

    @GetMapping("/surat-tugas-asesor/edit")
    public String showEditSuratTugas(Model model) { // 1. Tambahkan Model sebagai parameter

        return "pages/admin/surat/surat-tugas-edit";
    }

    @GetMapping("/surat-tugas-asesor/preview")
    public String previewSuratTugasHtml(Model model) {
        // 1. Masukkan Data Dummy (Palsu) untuk keperluan preview tampilan
        model.addAttribute("nomorSurat", "001/ST/PREVIEW/V/2026");

        // Format tanggal dummy
        DateTimeFormatter indoFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        model.addAttribute("tanggalSurat", LocalDate.now().format(indoFormat));

        model.addAttribute("genderSapaan", "Bapak");
        model.addAttribute("namaAsesor", "Muhammad Satria Arrozak (Contoh)");
        model.addAttribute("noMet", "MET.000.001234.2024");

        model.addAttribute("namaSkema", "Skema Sertifikasi Network Administrator");
        model.addAttribute("tanggalAsesmen", LocalDate.now().plusDays(3).format(indoFormat));
        model.addAttribute("namaTuk", "TUK Mandiri PPKD Jakarta Selatan");

        // 2. Return nama file template HTML yang ada di folder 'templates/pdf'
        return "pages/admin/surat/surat-tugas-template";
    }

    @GetMapping("/surat-tugas-asesor/delete/{id}")
    public String deleteSuratTugas(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Opsional: Hapus file fisik jika perlu
            // SuratTugas st = suratTugasRepository.findById(id).orElse(null);
            // if(st != null && st.getFilePath() != null) { ... logic hapus file ... }

            suratTugasRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Surat tugas berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus data.");
        }
        return "redirect:/admin/surat-tugas-asesor";
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

    // @GetMapping("/data-asesor")
    // public String showDataAsesor(Model model) {

    // return "pages/admin/asesors/asesor-list";
    // }

    @GetMapping("/data-asesor")
    public String showDataAsesor(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "desc") String sort) { // Param sort (asc/desc)

        // Tentukan Arah Sortir (Default DESC = Terbanyak ke Terkecil)
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Sorting berdasarkan hasil Count (alias kolom ke-2 di query / jumlahAsesmen)
        // Note: Sort by computed field di JPQL kadang tricky, kita gunakan index kolom
        // atau properti DTO jika didukung DB
        // Untuk aman di JPA standar, kita sort by 'COUNT(st)' di PageRequest agak
        // sulit,
        // jadi kita biarkan Pageable melakukan sort di memori atau gunakan query
        // native.
        // Tapi solusi terbaik JPA: JpaSort.unsafe(direction, "COUNT(st)")

        // Sederhananya, kita gunakan sort property 'jumlahAsesmen' jika menggunakan
        // custom implementation,
        // Tapi karena JPQL Pageable standar agak kaku dengan Agregat, kita gunakan
        // JpaSort unsafe atau logic default.
        // DISINI KITA GUNAKAN LOGIC SEDERHANA: SORT BY ID USER jika sort parameter
        // default,
        // tapi user minta sort by jumlah.

        // SOLUSI SORTING AGREGAT JPA (Menggunakan JpaSort.unsafe jika menggunakan
        // Spring Data JPA terbaru)
        // Atau kita handle sorting manual di Query string.
        // Agar tidak error "PropertyReferenceException", kita gunakan PageRequest tanpa
        // sort dulu di controllernya,
        // dan biarkan Query di Repository yang menangani ORDER BY nya jika perlu.

        // Namun, agar dinamis (Klik header), kita gunakan pendekatan JpaSort
        // (membutuhkan import org.springframework.data.jpa.domain.JpaSort)
        // Jika tidak ada JpaSort, kita gunakan Sort.unsorted() dan hardcode ORDER BY di
        // Query repository tadi.

        // UNTUK SAAT INI: Kita gunakan Sort berdasarkan ID User saja sebagai default
        // pageable,
        // Tapi karena Anda minta fitur sort jumlah, saya akan memodifikasi Repository
        // sedikit di bawah.

        Pageable pageable = PageRequest.of(page, size,
                org.springframework.data.jpa.domain.JpaSort.unsafe(direction, "COUNT(st)"));

        Page<AsesorListDto> pageAsesor = userRepository.findAsesorList(keyword, pageable);

        model.addAttribute("listAsesor", pageAsesor.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageAsesor.getTotalPages());
        model.addAttribute("totalItems", pageAsesor.getTotalElements());
        model.addAttribute("size", size);

        // Kirim status sort saat ini ke view untuk ikon panah
        model.addAttribute("currentSort", sort);
        model.addAttribute("reverseSort", sort.equals("asc") ? "desc" : "asc");

        return "pages/admin/asesors/asesor-list"; // Pastikan path HTML benar (asesor-list.html)
    }

}