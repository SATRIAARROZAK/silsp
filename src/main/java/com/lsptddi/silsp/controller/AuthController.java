// package com.lsptddi.silsp.controller;

// import com.lsptddi.silsp.dto.RegisterDto;
// import com.lsptddi.silsp.model.*;
// import com.lsptddi.silsp.repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.util.Collections;
// import java.util.HashSet;

// @Controller
// public class AuthController {

//     @Autowired
//     private RefEducationRepository educationRepository;
//     @Autowired
//     private RefJobTypeRepository jobTypeRepository;

//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private RoleRepository roleRepository;
//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @GetMapping("/register")
//     public String registerPage() {
//         return "register";
//     }

//     // 2. API CEK DUPLIKAT (Untuk Validasi Realtime JS)
//     @GetMapping("/api/check-duplicate")
//     @ResponseBody
//     public ResponseEntity<?> checkDuplicate(@RequestParam(required = false) String username,
//             @RequestParam(required = false) String email) {
//         if (username != null && userRepository.existsByUsername(username)) {
//             return ResponseEntity.ok(false); // Ada duplikat -> Invalid
//         }
//         if (email != null && userRepository.existsByEmail(email)) {
//             return ResponseEntity.ok(false); // Ada duplikat -> Invalid
//         }
//         return ResponseEntity.ok(true); // Aman -> Valid
//     }

//     // 3. PROSES REGISTRASI (POST)
//     @PostMapping("/register")
//     @ResponseBody
//     public ResponseEntity<?> registerProcess(@ModelAttribute RegisterDto dto) {
//         try {
//             // A. Validasi Server Side (Double Protection)
//             if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
//                 return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Role wajib dipilih!\"}");
//             }
//             if (userRepository.existsByUsername(dto.getUsername())) {
//                 return ResponseEntity.badRequest()
//                         .body("{\"status\":\"error\", \"message\":\"Username sudah digunakan!\"}");
//             }
//             if (userRepository.existsByEmail(dto.getEmail())) {
//                 return ResponseEntity.badRequest()
//                         .body("{\"status\":\"error\", \"message\":\"Email sudah digunakan!\"}");
//             }

//             // B. Mapping DTO ke Entity User
//             User user = new User();
//             user.setUsername(dto.getUsername());
//             user.setEmail(dto.getEmail());
//             // Enkripsi Password (Super Aman)
//             user.setPassword(passwordEncoder.encode(dto.getPassword()));

//             // // Set Role
//             // Role userRole = roleRepository.findByName(dto.getRoles().toUpperCase())
//             // .orElseThrow(() -> new RuntimeException("Role tidak ditemukan"));
//             // user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

//             // ------------------------------------------------------------------
//             // PERBAIKAN UTAMA: ROLE SENSITIVITY
//             // ------------------------------------------------------------------
//             // Hapus .toUpperCase(). Kita gunakan input mentah dari HTML ("Asesi"/"Asesor")
//             // Pastikan di database tabel 'roles' isinya: "Asesi" dan "Asesor" (bukan
//             // ASESI/ASESOR)
//             String roleName = dto.getRoles();

//             Role userRole = roleRepository.findByName(roleName)
//                     .orElseThrow(() -> new RuntimeException("Role '" + roleName
//                             + "' tidak ditemukan di Database. Pastikan Master Data Role sudah diisi dengan benar (Asesi/Asesor)."));

//             user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
//             // ------------------------------------------------------------------

//             // Data Pribadi
//             user.setFullName(dto.getFullName());
//             user.setNik(dto.getNik());
//             user.setBirthPlace(dto.getBirthPlace());
//             user.setBirthDate(dto.getBirthDate());
//             user.setGender(dto.getGender());
//             user.setCitizenship(dto.getCitizenship());
//             user.setPhoneNumber(dto.getPhoneNumber());

//             // LOGIKA NO. MET (Khusus Asesor)
//             // Format: "MET." + Input User
//             if ("Asesor".equalsIgnoreCase(dto.getRoles()) && dto.getNoMet() != null) {
//                 user.setNoMet("MET." + dto.getNoMet());
//             }

//             // A. Pendidikan Terakhir (Semua Role)
//             if (dto.getEducationId() != null) {
//                 RefEducation edu = educationRepository.findById(dto.getEducationId())
//                         .orElse(null); // Jika ID tidak valid, set null atau throw error
//                 user.setEducationId(edu);
//             }

//             // B. Jenis Pekerjaan (Khusus Asesor)
//             if ("Asesor".equals(roleName) && dto.getJobTypeId() != null) {
//                 RefJobType job = jobTypeRepository.findById(dto.getJobTypeId())
//                         .orElse(null);
//                 user.setJobTypeId(job);
//             }

//             // Relasi (Pastikan repository dipanggil jika perlu logic ambil object)
//             // Disini diasumsikan user entity menyimpan ID atau object relasi
//             // user.setLastEducation(...);
//             // user.setJobType(...);

//             // Wilayah
//             user.setProvinceId(dto.getProvinceId());
//             user.setCityId(dto.getCityId());
//             user.setDistrictId(dto.getDistrictId());
//             // user.setSubDistrictId(dto.getSubDistrictId());
//             user.setPostalCode(dto.getPostalCode());
//             user.setAddress(dto.getAddress());

//             // Tanda Tangan
//             user.setSignatureBase64(dto.getSignatureBase64());

//             // Simpan
//             userRepository.save(user);

//             return ResponseEntity.ok()
//                     .body("{\"status\":\"success\", \"message\":\"Registrasi Berhasil! Silakan Login.\"}");

//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.badRequest()
//                     .body("{\"status\":\"error\", \"message\":\"Gagal Mendaftar: " + e.getMessage() + "\"}");
//         }
//     }
// }

package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.dto.RegisterDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import com.lsptddi.silsp.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.UUID;

import java.util.Collections;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenResetPasswordRepository tokenRepository;
    @Autowired
    private EmailService emailService;

    // --- TAMBAHAN REPOSITORY BARU ---
    @Autowired
    private TypeEducationRepository educationRepository;
    @Autowired
    private TypePekerjaanRepository jobTypeRepository;
    // --------------------------------

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerPage(Authentication authentication) {
        // CEGAH USER LOGIN MASUK KE REGISTER
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/switch-role";
        }
        return "register";
    }

    @GetMapping("/api/check-duplicate")
    @ResponseBody
    public ResponseEntity<?> checkDuplicate(@RequestParam(required = false) String username,
            @RequestParam(required = false) String email, @RequestParam(required = false) String nik) { // Tambah
                                                                                                        // parameter
                                                                                                        // nik) {
        if (username != null && userRepository.existsByUsername(username))
            return ResponseEntity.ok(false);
        if (email != null && userRepository.existsByEmail(email))
            return ResponseEntity.ok(false);
        // if (nik != null && !nik.trim().isEmpty()) {
        // if (userRepository.existsByNik(nik))
        // return ResponseEntity.ok(false);
        // }
        if (nik != null && userRepository.existsByNik(nik))
            return ResponseEntity.ok(false);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerProcess(@ModelAttribute RegisterDto dto, HttpServletRequest request) {
        // public ResponseEntity<?> registerProcess(@ModelAttribute RegisterDto dto) {
        try {
            // 1. Validasi Awal
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
            if (userRepository.existsByNik(dto.getNik())) {
                return ResponseEntity.badRequest()
                        .body("{\"status\":\"error\", \"message\":\"NIK telah terdaftar!\"}");
            }

            // 2. Setup User
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            // user.setPassword(passwordEncoder.encode(dto.getPassword()));
            // SET PASSWORD SEMENTARA (RANDOM & KUAT)
            // User tidak akan tahu password ini, mereka wajib reset via email
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

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

            // Role Logic
            // String roleName = dto.getRoles();
            // Role userRole = roleRepository.findByName(roleName)
            // .orElseThrow(() -> new RuntimeException("Role tidak ditemukan: " +
            // roleName));
            // user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

            // 3. Data Pribadi
            user.setFullName(dto.getFullName());
            // user.setNik(dto.getNik());
            user.setBirthPlace(dto.getBirthPlace());
            user.setBirthDate(dto.getBirthDate());
            user.setGender(dto.getGender());
            user.setCitizenship(dto.getCitizenship());
            // user.setPhoneNumber(dto.getPhoneNumber());

            // 1. NIK: Hapus titik, simpan angka saja (32.73... -> 3273...)
            if (dto.getNik() != null) {
                user.setNik(dto.getNik().replaceAll("[^0-9]", ""));
            }

            // 2. NO TELP: Tambahkan '0' di depan (811... -> 0811...)
            if (dto.getPhoneNumber() != null) {
                String rawPhone = dto.getPhoneNumber().replaceAll("[^0-9]", ""); // Pastikan angka saja
                // Jika user iseng nulis 0 di depan, hapus dulu baru tambah 0 (biar ga double)
                if (rawPhone.startsWith("0"))
                    rawPhone = rawPhone.substring(1);
                user.setPhoneNumber("0" + rawPhone);
            }

            // // 4. Data Asesor (No MET)
            // if ("Asesor".equals(roleName) && dto.getNoMet() != null) {
            // user.setNoMet("MET. " + dto.getNoMet());
            // }

            // // -----------------------------------------------------------
            // // PERBAIKAN UTAMA: SIMPAN PENDIDIKAN & PEKERJAAN
            // // -----------------------------------------------------------

            // // A. Pendidikan Terakhir (Semua Role)
            // if (dto.getEducationId() != null) {
            // RefEducation edu = educationRepository.findById(dto.getEducationId())
            // .orElse(null); // Jika ID tidak valid, set null atau throw error
            // user.setLastEducation(edu);
            // }

            // // B. Jenis Pekerjaan (Khusus Asesor)
            // if ("Asesor".equals(roleName) && dto.getJobTypeId() != null) {
            // RefJobType job = jobTypeRepository.findById(dto.getJobTypeId())
            // .orElse(null);
            // user.setJobType(job);
            // }

            // LOGIKA NO. MET (Khusus Asesor)
            // // Format: "MET." + Input User
            if ("Asesor".equalsIgnoreCase(dto.getRoles()) && dto.getNoMet() != null) {
                user.setNoMet("MET." + dto.getNoMet());
            }

            // A. Pendidikan Terakhir (Semua Role)
            if (dto.getEducationId() != null) {
                TypeEducation edu = educationRepository.findById(dto.getEducationId())
                        .orElse(null); // Jika ID tidak valid, set null atau throw error
                user.setEducationId(edu);
            }

            // B. Jenis Pekerjaan (Khusus Asesor)
            if ("Asesor".equals(roleName) && dto.getJobTypeId() != null) {
                TypePekerjaan job = jobTypeRepository.findById(dto.getJobTypeId())
                        .orElse(null);
                user.setJobTypeId(job);
            }

            // -----------------------------------------------------------

            // 6. Wilayah
            user.setProvinceId(dto.getProvinceId());
            user.setCityId(dto.getCityId());
            user.setDistrictId(dto.getDistrictId());
            // user.setSubDistrictId(dto.getSubDistrictId());
            user.setPostalCode(dto.getPostalCode());
            user.setAddress(dto.getAddress());

            // 7. Tanda Tangan
            user.setSignatureBase64(dto.getSignatureBase64());

            userRepository.save(user);

            // ============================================================
            // 3. PROSES AKTIVASI EMAIL (BUAT TOKEN & KIRIM EMAIL)
            // ============================================================

            // A. Buat Token
            String token = UUID.randomUUID().toString();
            TokenResetPassword myToken = new TokenResetPassword(token, user);
            tokenRepository.save(myToken);

            // B. Buat Link Reset
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String activationLink = appUrl + "/reset-password?token=" + token;

            // C. Kirim Email
            String subject = "Registrasi Anda Berhasil - SILSP";
            String body = "Selamat Datang " + user.getFullName() + ",\n\n" +
                    "Pendaftaran akun Anda berhasil!\n" +
                    "Langkah terakhir, silakan klik tautan di bawah ini untuk membuat kata sandi Anda:\n" +
                    activationLink + "\n\n\n" +

                    "Admin\n" +
                    "LSP Teknologi Data Digital Indonesia";
            emailService.sendEmail(user.getEmail(), subject, body);

            return ResponseEntity.ok()
                    .body("{\"status\":\"success\", \"message\":\"Registrasi Berhasil!<br>Silakan cek email <b>"
                            + dto.getEmail() + "</b> untuk membuat kata sandi.\"}");

            // return ResponseEntity.ok()
            // .body("{\"status\":\"success\", \"message\":\"Registrasi Berhasil! Silakan
            // Login.\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\":\"error\", \"message\":\"Gagal: " + e.getMessage() + "\"}");
        }
    }
}