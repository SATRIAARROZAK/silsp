package com.lsptddi.silsp.controller.asesi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import com.lsptddi.silsp.dto.SertifikasiRequestDto;
// DTO (DATA TRANFERS OBJECK)
import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.dto.SertifikasiRequestDto;
import com.lsptddi.silsp.model.PermohonanSertifikasi;
// import com.lsptddi.silsp.model.PermohonanSertifikasi;
// MODEL
import com.lsptddi.silsp.model.User; // Import Model User Asli

// REPOSITORY
import com.lsptddi.silsp.repository.*;
// import com.lsptddi.silsp.repository.UserRepository; // Import Repo
// import com.lsptddi.silsp.repository.SkemaRepository;
// import com.lsptddi.silsp.repository.TypePekerjaanRepository;

// Service
import com.lsptddi.silsp.service.PermohonanService;

// Import java
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

// LIBLARY UMUM org
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

// librarry umum com
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/asesi")
public class AsesiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkemaRepository skemaRepository;

    @Autowired
    private TypePekerjaanRepository typePekerjaanRepository;

    @Autowired
    private PermohonanSertifikasiRepository permohonanRepository;

    @Autowired
    private PermohonanService permohonanService;

    // Method ini akan dijalankan sebelum setiap request di controller ini
    // Fungsinya mengambil User asli dari database berdasarkan siapa yang login
    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName(); // Ambil username yang login
            User user = userRepository.findByUsername(username)
                    .orElse(null); // Cari di DB

            // Masukkan object User asli ke Thymeleaf
            // Jadi di HTML bisa panggil ${user.fullName}, ${user.email}, dll
            model.addAttribute("user", user);
            model.addAttribute("currentRole", "Asesi");
        }
    }

    @GetMapping("/dashboard")
    public String index() {
        return "pages/asesi/dashboard";
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

        // Mapping Relasi ID (3NF)
        // Mapping Relasi ID (3NF)
        if (user.getEducationId() != null)
            dto.setEducationId(user.getEducationId().getId());
        if (user.getJobTypeId() != null)
            dto.setJobTypeId(user.getJobTypeId().getId());

        dto.setCompanyName(user.getCompanyName());
        dto.setPosition(user.getPosition());
        dto.setOfficePhone(user.getOfficePhone());
        dto.setOfficeEmail(user.getOfficeEmail());
        dto.setOfficeFax(user.getOfficeFax());
        dto.setOfficeAddress(user.getOfficeAddress());

        // dto.setAvatar(user.getAvatar()); // Removed: setAvatar expects MultipartFile,
        // not String
        dto.setSignatureBase64(user.getSignatureBase64());

        dto.setProvinceId(user.getProvinceId());
        dto.setCityId(user.getCityId());
        dto.setDistrictId(user.getDistrictId());

        model.addAttribute("userDto", dto);
        return "pages/asesi/asesi-profile"; // Mengarah ke file HTML shared
    }

    @GetMapping("/daftar-sertifikasi")
    public String showSertifikasiList(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Ambil list permohonan user ini
        List<PermohonanSertifikasi> listPermohonan = permohonanRepository.findByAsesiOrderByTanggalPermohonanDesc(user);

        model.addAttribute("listPermohonan", listPermohonan);

        return "pages/asesi/sertifikasi/sertifikasi-list";
    }

    // HALAMAN PENDAFTARAN (FORM WIZARD)
    @GetMapping("/daftar")
    public String showDaftarUji(Model model, Principal principal) {
        // 1. Load Data User untuk Tab 2 (Data Pemohon)
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Mapping User ke DTO agar mudah ditampilkan di form (Read-only / Auto-fill)
        UserProfileDto userDto = new UserProfileDto();
        userDto.setNik(user.getNik());
        userDto.setFullName(user.getFullName());
        userDto.setBirthPlace(user.getBirthPlace());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setGender(user.getGender());
        userDto.setAddress(user.getAddress());
        userDto.setPostalCode(user.getPostalCode());
        // userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setEmail(user.getEmail());

        // 2. NO TELP: Hapus '0' di depan agar di form jadi (811...)
        if (user.getPhoneNumber() != null && user.getPhoneNumber().startsWith("0")) {
            userDto.setPhoneNumber(user.getPhoneNumber().substring(1));
        } else {
            userDto.setPhoneNumber(user.getPhoneNumber());
        }

        // Data Pekerjaan
        userDto.setCompanyName(user.getCompanyName());
        userDto.setPosition(user.getPosition());
        userDto.setOfficeAddress(user.getOfficeAddress());
        userDto.setOfficePhone(user.getOfficePhone());
        userDto.setOfficeEmail(user.getOfficeEmail());
        userDto.setOfficeFax(user.getOfficeFax());

        // ID Relasi
        if (user.getJobTypeId() != null)
            userDto.setJobTypeId(user.getJobTypeId().getId());
        if (user.getEducationId() != null)
            userDto.setEducationId(user.getEducationId().getId());

        userDto.setProvinceId(user.getProvinceId());
        userDto.setCityId(user.getCityId());
        userDto.setDistrictId(user.getDistrictId());

        // Kirim Data
        model.addAttribute("asesiDto", userDto);

        // 2. Load List Skema (Untuk Dropdown Tab 1)
        model.addAttribute("listSkema", skemaRepository.findAll());

        // 3. Load Referensi Pekerjaan (Untuk Dropdown Tab 2)
        model.addAttribute("listPekerjaan", typePekerjaanRepository.findAll());

        return "pages/asesi/sertifikasi/sertifikasi-add";
    }

    @PostMapping("/sertifikasi/save")
    @ResponseBody
    public ResponseEntity<?> saveSertifikasi(
            MultipartHttpServletRequest request,
            Principal principal) {

        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            // --- PERBAIKAN DI SINI ---
            ObjectMapper mapper = new ObjectMapper();
            // Mendaftarkan modul untuk menangani LocalDate/LocalDateTime
            mapper.registerModule(new JavaTimeModule());
            // Opsional: Agar tidak error jika ada field JSON yang tidak ada di Java Class
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // -------------------------

            // 1. AMBIL JSON UTAMA
            String jsonStr = request.getParameter("jsonData");
            if (jsonStr == null || jsonStr.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\": \"Data JSON kosong!\"}");
            }

            // Konversi JSON String ke Object Java
            SertifikasiRequestDto payload = mapper.readValue(jsonStr, SertifikasiRequestDto.class);

            // 2. AMBIL FILES
            Map<String, MultipartFile> fileMap = request.getFileMap();

            // 3. PANGGIL SERVICE (Kirim Payload + Files)
            permohonanService.processPermohonanJson(user, payload, fileMap);

            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"Pendaftaran berhasil dikirim!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Gagal: " + e.getMessage() + "\"}");
        }
    }

    private Long parseLongSafe(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("null")) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}