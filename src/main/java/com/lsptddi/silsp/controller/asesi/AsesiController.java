package com.lsptddi.silsp.controller.asesi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsptddi.silsp.dto.PermohonanDto;
// import com.lsptddi.silsp.dto.SertifikasiRequestDto;
// DTO (DATA TRANFERS OBJECK)
import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.model.Permohonan;
import com.lsptddi.silsp.model.PermohonanAsesmen;
import com.lsptddi.silsp.model.PermohonanBukti;
// import com.lsptddi.silsp.model.PermohonanSertifikasi;
import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.Skema;
// import com.lsptddi.silsp.model.AsesmenMandiri;
// import com.lsptddi.silsp.model.Pendaftaran;
// import com.lsptddi.silsp.model.PendaftaranBukti;
// import com.lsptddi.silsp.model.PendaftaranSyarat;
// MODEL
import com.lsptddi.silsp.model.User; // Import Model User Asli

// REPOSITORY
import com.lsptddi.silsp.repository.*;
// import com.lsptddi.silsp.repository.UserRepository; // Import Repo
// import com.lsptddi.silsp.repository.SkemaRepository;
// import com.lsptddi.silsp.repository.TypePekerjaanRepository;

// Service
import com.lsptddi.silsp.service.FileStorageService;

// Import java
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

// LIBLARY UMUM org
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

// librarry umum com
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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
    private PermohonanRepository permohonanRepository;

    // @Autowired
    // private PendaftaranRepository pendaftaranRepository;
    // @Autowired
    // private PendaftaranSyaratRepository pendaftaranSyaratRepository;
    @Autowired
    private PermohonanBuktiRepository permohonanBuktiRepository;
    @Autowired
    private PermohonanAsesmenRepository permohonanAsesmenRepository;
    @Autowired
    private TypeEducationRepository educationRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private FileStorageService saveFileService;

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

    @PostMapping("/sertifikasi/save")
    @ResponseBody
    public ResponseEntity<?> savePermohonan(
            @RequestPart("data") String dataJson, // JSON String dari DTO
            @RequestPart(value = "files", required = false) List<MultipartFile> files // Semua file
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PermohonanDto dto = mapper.readValue(dataJson, PermohonanDto.class);
            User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .get();

            // 1. UPDATE DATA PEMOHON (Tab 2)
            // Logic update user profile berdasarkan dto.getDataPemohon()...
            if (dto.getDataPemohon().getCompanyName() == null || dto.getDataPemohon().getCompanyName().isEmpty()) {
                // Jika tidak bekerja, set detail null
                user.setCompanyName(null);
                user.setPosition(null);
                // ... set null field lain
            } else {
                user.setCompanyName(dto.getDataPemohon().getCompanyName());
                // ... update field lain
            }
            userRepository.save(user);

            // 2. SAVE HEADER PERMOHONAN
            Permohonan permohonan = new Permohonan();
            permohonan.setAsesi(user);
            permohonan.setSkema(skemaRepository.findById(dto.getSkemaId()).get());
            permohonan.setJadwal(scheduleRepository.findById(dto.getJadwalId()).get());
            permohonan.setTujuanAsesmen(dto.getTujuanAsesmen());
            permohonan.setStatus("MENUNGGU_VERIFIKASI");
            permohonan = permohonanRepository.save(permohonan); // Save dulu untuk dapat ID

            // 3. HANDLE FILE & BUKTI (Tab 3, 4, 5)
            // Ini tricky. Kita harus mencocokkan urutan File di List<MultipartFile>
            // dengan urutan di dto.getListBukti() atau menggunakan logika penamaan file.
            // Solusi sederhana: Asumsikan urutan listBukti DTO sama dengan urutan Files
            // yang dikirim.

            if (files != null && dto.getListBukti() != null) {
                int fileIndex = 0;
                for (PermohonanDto.BuktiDto bDto : dto.getListBukti()) {
                    if (fileIndex < files.size()) {
                        MultipartFile file = files.get(fileIndex);
                        String path = saveFileService.save(file); // Method simpan file Anda

                        PermohonanBukti bukti = new PermohonanBukti();
                        bukti.setPermohonan(permohonan);
                        bukti.setJenisBukti(bDto.getJenis());
                        bukti.setNamaBukti(bDto.getNama());
                        bukti.setFilePath(path);
                        permohonanBuktiRepository.save(bukti);

                        fileIndex++;
                    }
                }
            }

            // 4. SAVE ASESMEN (Tab 7)
            if (dto.getListAsesmen() != null) {
                for (PermohonanDto.AsesmenDto aDto : dto.getListAsesmen()) {
                    PermohonanAsesmen asesmen = new PermohonanAsesmen();
                    asesmen.setPermohonan(permohonan);
                    asesmen.setKodeUnit(aDto.getKodeUnit());
                    asesmen.setNoElemen(aDto.getNoElemen());
                    asesmen.setStatusKompetensi(aDto.getStatus());

                    // Join list ID menjadi string misal "portofolio_1,portofolio_2"
                    if (aDto.getBuktiRefIds() != null) {
                        asesmen.setBuktiRelevanIds(String.join(",", aDto.getBuktiRefIds()));
                    }
                    permohonanAsesmenRepository.save(asesmen);
                }
            }

            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"Pendaftaran berhasil dikirim!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Gagal: " + e.getMessage());
        }
    }

}