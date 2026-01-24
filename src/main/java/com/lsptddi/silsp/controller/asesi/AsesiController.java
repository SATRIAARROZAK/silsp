package com.lsptddi.silsp.controller.asesi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import com.lsptddi.silsp.dto.SertifikasiRequestDto;
// DTO (DATA TRANFERS OBJECK)
import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.dto.SertifikasiRequestDto;
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
    public String showSertifikasiList() {
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

    // Helper untuk parsing Long dengan aman (mencegah error Cannot parse null
    // string)
    // private Long parseLongSafe(String value) {
    // if (value == null || value.trim().isEmpty() || value.equals("null")) {
    // return null;
    // }
    // try {
    // return Long.parseLong(value);
    // } catch (NumberFormatException e) {
    // return null;
    // }
    // }

    // @PostMapping("/sertifikasi/save")
    // @ResponseBody
    // public ResponseEntity<?> saveSertifikasi(
    // MultipartHttpServletRequest request,
    // Principal principal) {

    // try {
    // User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    // // 1. Ambil Data Dasar dengan Parsing Aman
    // Long skemaId = parseLongSafe(request.getParameter("skemaId"));
    // Long jadwalId = parseLongSafe(request.getParameter("jadwalId"));

    // // Validasi Manual: Skema & Jadwal Wajib Ada
    // if (skemaId == null || jadwalId == null) {
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Skema dan Jadwal wajib
    // dipilih!\"}");
    // }

    // // String sumberAnggaranId = request.getParameter("sumberAnggaran");
    // // String pemberiAnggaranId = request.getParameter("pemberiAnggaran");
    // Long sumberId = parseLongSafe(request.getParameter("sumberAnggaranId"));
    // Long pemberiId = parseLongSafe(request.getParameter("pemberiAnggaranId"));
    // String tujuanAsesmen = request.getParameter("tujuanAsesmen");

    // // 2. Ambil Data Pemohon (Update Profile)
    // UserProfileDto userDto = new UserProfileDto();
    // userDto.setNik(request.getParameter("nik"));
    // userDto.setFullName(request.getParameter("fullName"));
    // userDto.setBirthPlace(request.getParameter("birthPlace"));

    // // Handle Tanggal Lahir Aman
    // String tglLahirStr = request.getParameter("birthDate");
    // if (tglLahirStr != null && !tglLahirStr.isEmpty()) {
    // userDto.setBirthDate(LocalDate.parse(tglLahirStr));
    // }

    // userDto.setGender(request.getParameter("gender"));
    // // ... set field lainnya ...

    // // Handle Job Type & Education Aman
    // userDto.setJobTypeId(parseLongSafe(request.getParameter("jobType")));
    // userDto.setEducationId(parseLongSafe(request.getParameter("educationId")));
    // // Pastikan name di HTML
    // // educationId bukan
    // // selectPendidikan

    // userDto.setCompanyName(request.getParameter("companyName"));
    // userDto.setPosition(request.getParameter("position"));
    // userDto.setOfficePhone(request.getParameter("officePhone"));
    // userDto.setOfficeEmail(request.getParameter("officeEmail"));
    // userDto.setOfficeFax(request.getParameter("officeFax"));
    // userDto.setOfficeAddress(request.getParameter("officeAddress"));

    // // ... Mapping wilayah ...
    // userDto.setProvinceId(request.getParameter("provinceId"));
    // userDto.setCityId(request.getParameter("cityId"));
    // userDto.setDistrictId(request.getParameter("districtId"));
    // userDto.setAddress(request.getParameter("address"));
    // userDto.setAddress(request.getParameter("address"));
    // userDto.setPostalCode(request.getParameter("postalCode"));
    // userDto.setPhoneNumber(request.getParameter("phoneNumber"));
    // userDto.setEmail(request.getParameter("email"));

    // // Map<String, MultipartFile> fileMap = request.getFileMap();
    // // Map<String, String> apl02Data = new HashMap<>(); // K/BK
    // // Map<String, String[]> apl02Bukti = new HashMap<>(); // Bukti (Array
    // string)

    // // // Loop parameter request
    // // // Format JS: "kompeten_elemen_12" -> Value "K"
    // // // Format JS: "bukti_elemen_12" -> Value ["portofolio_1", "portofolio_2"]

    // // request.getParameterMap().forEach((key, value) -> {
    // // if (key.startsWith("kompeten_elemen_")) {
    // // apl02Data.put(key, value[0]);
    // // } else if (key.startsWith("bukti_elemen_")) {
    // // // Karena select2 multiple, value adalah array
    // // // Kita simpan di map khusus bukti
    // // apl02Bukti.put(key, value);
    // // }
    // // });

    // // // 4. Panggil Service (Update parameter service)
    // // permohonanService.processPermohonan(
    // // user, skemaId, jadwalId, sumberId, pemberiId, tujuanAsesmen,
    // // userDto, fileMap, apl02Data, apl02Bukti);

    // // 3. PISAHKAN DATA INPUT
    // Map<String, MultipartFile> fileMap = request.getFileMap();
    // Map<String, String> apl02Data = new HashMap<>(); // Untuk K/BK (Single Value)
    // Map<String, String[]> apl02Bukti = new HashMap<>(); // Untuk Bukti (Multiple
    // Value)

    // // Loop semua parameter request
    // Map<String, String[]> paramMap = request.getParameterMap();

    // for (String key : paramMap.keySet()) {
    // // Ambil Data Checkbox K/BK
    // if (key.startsWith("kompeten_elemen_")) {
    // // Checkbox value cuma 1 string ("K" atau "BK")
    // apl02Data.put(key, paramMap.get(key)[0]);
    // }
    // // Ambil Data Bukti Relevan (Select2 Multiple)
    // else if (key.startsWith("bukti_elemen_")) {
    // // Select2 multiple mengirim array string
    // apl02Bukti.put(key, paramMap.get(key));
    // }
    // }

    // // 4. PANGGIL SERVICE
    // permohonanService.processPermohonan(
    // user, skemaId, jadwalId, sumberId, pemberiId, tujuanAsesmen,
    // userDto, fileMap, apl02Data, apl02Bukti);

    // return ResponseEntity.ok()
    // .body("{\"status\": \"success\", \"message\": \"Pendaftaran berhasil
    // dikirim!\"}");

    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Gagal: " + e.getMessage() +
    // "\"}");
    // }
    // }

    // ... imports (pastikan import SertifikasiRequestDto sudah ada)

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
    // @PostMapping("/sertifikasi/save")
    // @ResponseBody
    // public ResponseEntity<?> saveSertifikasi(
    // MultipartHttpServletRequest request,
    // Principal principal) {

    // try {
    // User user = userRepository.findByUsername(principal.getName()).orElseThrow();
    // ObjectMapper mapper = new ObjectMapper(); // JSON Mapper

    // // 1. Data Dasar
    // Long skemaId = parseLongSafe(request.getParameter("skemaId"));
    // Long jadwalId = parseLongSafe(request.getParameter("jadwalId"));

    // if (skemaId == null || jadwalId == null) {
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Skema/Jadwal tidak valid!\"}");
    // }

    // // 2. Data Pemohon (User Profile)
    // // ... (Kode mapping userDto TETAP SAMA seperti sebelumnya) ...
    // UserProfileDto userDto = new UserProfileDto();
    // userDto.setNik(request.getParameter("nik"));
    // userDto.setFullName(request.getParameter("fullName"));
    // userDto.setBirthPlace(request.getParameter("birthPlace"));

    // // Handle Tanggal Lahir Aman
    // String tglLahirStr = request.getParameter("birthDate");
    // if (tglLahirStr != null && !tglLahirStr.isEmpty()) {
    // userDto.setBirthDate(LocalDate.parse(tglLahirStr));
    // }

    // userDto.setGender(request.getParameter("gender"));
    // userDto.setJobTypeId(parseLongSafe(request.getParameter("jobType")));
    // userDto.setEducationId(parseLongSafe(request.getParameter("educationId")));

    // userDto.setCompanyName(request.getParameter("companyName"));
    // userDto.setPosition(request.getParameter("position"));
    // userDto.setOfficePhone(request.getParameter("officePhone"));
    // userDto.setOfficeEmail(request.getParameter("officeEmail"));
    // userDto.setOfficeFax(request.getParameter("officeFax"));
    // userDto.setOfficeAddress(request.getParameter("officeAddress"));

    // userDto.setProvinceId(request.getParameter("provinceId"));
    // userDto.setCityId(request.getParameter("cityId"));
    // userDto.setDistrictId(request.getParameter("districtId"));
    // userDto.setAddress(request.getParameter("address"));
    // userDto.setPostalCode(request.getParameter("postalCode"));
    // userDto.setPhoneNumber(request.getParameter("phoneNumber"));
    // userDto.setEmail(request.getParameter("email"));

    // // 3. File Map
    // Map<String, MultipartFile> fileMap = request.getFileMap();

    // // 4. AMBIL DATA APL-02 DARI JSON (SOLUSI ERROR)
    // String jsonApl02 = request.getParameter("apl02Json");
    // List<Map<String, Object>> apl02List = new ArrayList<>();

    // if (jsonApl02 != null && !jsonApl02.isEmpty()) {
    // apl02List = mapper.readValue(jsonApl02, new TypeReference<List<Map<String,
    // Object>>>() {
    // });
    // } else {
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Data asesmen mandiri
    // kosong!\"}");
    // }

    // Long sumberId = parseLongSafe(request.getParameter("sumberAnggaranId"));
    // Long pemberiId = parseLongSafe(request.getParameter("pemberiAnggaranId"));
    // String tujuanAsesmen = request.getParameter("tujuanAsesmen");
    // // Map<String, String[]> apl02Bukti = new HashMap<>(); // Untuk Bukti
    // (Multiple
    // // Value)
    // // // Loop semua parameter request
    // // Map<String, String[]> paramMap = request.getParameterMap();
    // // for (String key : paramMap.keySet()) {
    // // // Ambil Data Bukti Relevan (Select2 Multiple)
    // // if (key.startsWith("bukti_elemen_")) {
    // // // Select2 multiple mengirim array string
    // // apl02Bukti.put(key, paramMap.get(key));
    // // }
    // // }

    // // 5. Panggil Service (Method Signature Diubah sedikit)
    // permohonanService.processPermohonan(
    // user, skemaId, jadwalId, sumberId, pemberiId, tujuanAsesmen,
    // userDto, fileMap, apl02List); // Kirim List JSON
    // return ResponseEntity.ok()
    // .body("{\"status\": \"success\", \"message\": \"Pendaftaran berhasil
    // dikirim!\"}");

    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Gagal: " + e.getMessage() +
    // "\"}");
    // }
    // }

    // @PostMapping("/sertifikasi/save")
    // @ResponseBody
    // public ResponseEntity<?> saveSertifikasi(
    // MultipartHttpServletRequest request,
    // Principal principal) {

    // try {
    // User user = userRepository.findByUsername(principal.getName()).orElseThrow();

    // // 1. Ambil Data Dasar dengan Parsing Aman
    // Long skemaId = parseLongSafe(request.getParameter("skemaId"));
    // Long jadwalId = parseLongSafe(request.getParameter("jadwalId"));

    // // Validasi Manual: Skema & Jadwal Wajib Ada
    // if (skemaId == null || jadwalId == null) {
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Skema dan Jadwal wajib
    // dipilih!\"}");
    // }

    // Long sumberId = parseLongSafe(request.getParameter("sumberAnggaranId"));
    // Long pemberiId = parseLongSafe(request.getParameter("pemberiAnggaranId"));
    // String tujuanAsesmen = request.getParameter("tujuanAsesmen");

    // // 2. Ambil Data Pemohon (Update Profile)
    // UserProfileDto userDto = new UserProfileDto();
    // userDto.setNik(request.getParameter("nik"));
    // userDto.setFullName(request.getParameter("fullName"));
    // userDto.setBirthPlace(request.getParameter("birthPlace"));

    // // Handle Tanggal Lahir Aman
    // String tglLahirStr = request.getParameter("birthDate");
    // if (tglLahirStr != null && !tglLahirStr.isEmpty()) {
    // userDto.setBirthDate(LocalDate.parse(tglLahirStr));
    // }

    // userDto.setGender(request.getParameter("gender"));
    // userDto.setJobTypeId(parseLongSafe(request.getParameter("jobType")));
    // userDto.setEducationId(parseLongSafe(request.getParameter("educationId")));

    // userDto.setCompanyName(request.getParameter("companyName"));
    // userDto.setPosition(request.getParameter("position"));
    // userDto.setOfficePhone(request.getParameter("officePhone"));
    // userDto.setOfficeEmail(request.getParameter("officeEmail"));
    // userDto.setOfficeFax(request.getParameter("officeFax"));
    // userDto.setOfficeAddress(request.getParameter("officeAddress"));

    // userDto.setProvinceId(request.getParameter("provinceId"));
    // userDto.setCityId(request.getParameter("cityId"));
    // userDto.setDistrictId(request.getParameter("districtId"));
    // userDto.setAddress(request.getParameter("address"));
    // userDto.setPostalCode(request.getParameter("postalCode"));
    // userDto.setPhoneNumber(request.getParameter("phoneNumber"));
    // userDto.setEmail(request.getParameter("email"));

    // // 3. PISAHKAN DATA INPUT TAB 7
    // Map<String, MultipartFile> fileMap = request.getFileMap();
    // Map<String, String> apl02Data = new HashMap<>(); // Untuk K/BK (Single Value)
    // Map<String, String[]> apl02Bukti = new HashMap<>(); // Untuk Bukti (Multiple
    // Value)

    // // Loop semua parameter request
    // Map<String, String[]> paramMap = request.getParameterMap();

    // System.out.println("=== DEBUG: PARAMETER YANG DITERIMA ===");

    // for (String key : paramMap.keySet()) {
    // String[] values = paramMap.get(key);

    // // DEBUG: Print semua parameter
    // System.out.println("Key: " + key + " | Values: " + String.join(", ",
    // values));

    // // Ambil Data Rekomendasi K/BK (Radio Button)
    // if (key.startsWith("kompeten_elemen_")) {
    // // Radio button hanya mengirim 1 value: "K" atau "BK"
    // apl02Data.put(key, values[0]);
    // System.out.println(" -> Ditambahkan ke apl02Data: " + key + " = " +
    // values[0]);
    // }
    // // Ambil Data Bukti Relevan (Select2 Multiple)
    // else if (key.startsWith("bukti_elemen_")) {
    // // Select2 multiple mengirim array string (ID portofolio)
    // apl02Bukti.put(key, values);
    // System.out.println(" -> Ditambahkan ke apl02Bukti: " + key + " = " +
    // String.join(", ", values));
    // }
    // }

    // // Validasi: Pastikan ada data Tab 7
    // if (apl02Data.isEmpty()) {
    // System.out.println("WARNING: Tidak ada data rekomendasi K/BK yang
    // diterima!");
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Data asesmen mandiri (Tab 7)
    // tidak lengkap. Mohon isi rekomendasi K/BK untuk semua elemen.\"}");
    // }

    // System.out.println("=== SUMMARY ===");
    // System.out.println("Total Rekomendasi K/BK: " + apl02Data.size());
    // System.out.println("Total Bukti Relevan: " + apl02Bukti.size());

    // // 4. PANGGIL SERVICE
    // permohonanService.processPermohonan(
    // user, skemaId, jadwalId, sumberId, pemberiId, tujuanAsesmen,
    // userDto, fileMap, apl02Data, apl02Bukti);

    // return ResponseEntity.ok()
    // .body("{\"status\": \"success\", \"message\": \"Pendaftaran berhasil
    // dikirim!\"}");

    // } catch (Exception e) {
    // e.printStackTrace();
    // System.out.println("ERROR: " + e.getMessage());
    // return ResponseEntity.badRequest()
    // .body("{\"status\": \"error\", \"message\": \"Gagal: " + e.getMessage() +
    // "\"}");
    // }
    // }

    // Helper untuk parsing Long dengan aman
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