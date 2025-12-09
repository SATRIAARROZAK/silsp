package com.lsptddi.silsp.controller;

import org.springframework.stereotype.Controller;

import com.lsptddi.silsp.dto.RegisterDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RefEducationRepository educationRepository;
    @Autowired
    private RefJobTypeRepository jobTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Menampilkan halaman login kustom.
     * 
     * @return String nama template login (login.html)
     */
    @GetMapping("/login")
    public String loginPage() {
        // Mengembalikan nama file HTML dari folder 'templates'
        return "login";
    }

    // --- TAMBAHKAN METODE INI ---
    /**
     * Menampilkan halaman registrasi kustom.
     * 
     * @return String nama template (register-custom.html)
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // 2. API CEK DUPLIKAT (Untuk Validasi Realtime JS)
    @GetMapping("/api/check-duplicate")
    @ResponseBody
    public ResponseEntity<?> checkDuplicate(@RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        if (username != null && userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(false); // Ada duplikat -> Invalid
        }
        if (email != null && userRepository.existsByEmail(email)) {
            return ResponseEntity.ok(false); // Ada duplikat -> Invalid
        }
        return ResponseEntity.ok(true); // Aman -> Valid
    }

    // 3. PROSES REGISTRASI (POST)
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerProcess(@ModelAttribute RegisterDto dto) {
        try {
            // A. Validasi Server Side (Double Protection)
            if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Role wajib dipilih!\"}");
            }
            if (userRepository.existsByUsername(dto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body("{\"status\":\"error\", \"message\":\"Username sudah digunakan!\"}");
            }
            if (userRepository.existsByEmail(dto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("{\"status\":\"error\", \"message\":\"Email sudah digunakan!\"}");
            }

            // B. Mapping DTO ke Entity User
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            // Enkripsi Password (Super Aman)
            user.setPassword(passwordEncoder.encode(dto.getPassword()));

            // // Set Role
            // Role userRole = roleRepository.findByName(dto.getRoles().toUpperCase())
            // .orElseThrow(() -> new RuntimeException("Role tidak ditemukan"));
            // user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

            // ------------------------------------------------------------------
            // PERBAIKAN UTAMA: ROLE SENSITIVITY
            // ------------------------------------------------------------------
            // Hapus .toUpperCase(). Kita gunakan input mentah dari HTML ("Asesi"/"Asesor")
            // Pastikan di database tabel 'roles' isinya: "Asesi" dan "Asesor" (bukan
            // ASESI/ASESOR)
            String roleName = dto.getRoles();

            Role userRole = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role '" + roleName
                            + "' tidak ditemukan di Database. Pastikan Master Data Role sudah diisi dengan benar (Asesi/Asesor)."));

            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            // ------------------------------------------------------------------

            // Data Pribadi
            user.setFullName(dto.getFullName());
            user.setNik(dto.getNik());
            user.setBirthPlace(dto.getBirthPlace());
            user.setBirthDate(dto.getBirthDate());
            user.setGender(dto.getGender());
            user.setCitizenship(dto.getCitizenship());
            user.setPhoneNumber(dto.getPhoneNumber());

            // LOGIKA NO. MET (Khusus Asesor)
            // Format: "MET." + Input User
            if ("Asesor".equalsIgnoreCase(dto.getRoles()) && dto.getNoMet() != null) {
                user.setNoMet("MET." + dto.getNoMet());
            }

            // A. Pendidikan Terakhir (Semua Role)
            if (dto.getEducationId() != null) {
                RefEducation edu = educationRepository.findById(dto.getEducationId())
                        .orElse(null); // Jika ID tidak valid, set null atau throw error
                user.setEducationId(edu);
            }

            // B. Jenis Pekerjaan (Khusus Asesor)
            if ("Asesor".equals(roleName) && dto.getJobTypeId() != null) {
                RefJobType job = jobTypeRepository.findById(dto.getJobTypeId())
                        .orElse(null);
                user.setJobTypeId(job);
            }

            // Relasi (Pastikan repository dipanggil jika perlu logic ambil object)
            // Disini diasumsikan user entity menyimpan ID atau object relasi
            // user.setLastEducation(...);
            // user.setJobType(...);

            // Wilayah
            user.setProvinceId(dto.getProvinceId());
            user.setCityId(dto.getCityId());
            user.setDistrictId(dto.getDistrictId());
            user.setSubDistrictId(dto.getSubDistrictId());
            user.setPostalCode(dto.getPostalCode());
            user.setAddress(dto.getAddress());

            // Tanda Tangan
            user.setSignatureBase64(dto.getSignatureBase64());

            // Simpan
            userRepository.save(user);

            return ResponseEntity.ok()
                    .body("{\"status\":\"success\", \"message\":\"Registrasi Berhasil! Silakan Login.\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\":\"error\", \"message\":\"Gagal Mendaftar: " + e.getMessage() + "\"}");
        }
    }

    /**
     * (Opsional) Mengarahkan halaman root ("/") ke halaman login.
     * 
     * @return String redirect ke /login
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

}