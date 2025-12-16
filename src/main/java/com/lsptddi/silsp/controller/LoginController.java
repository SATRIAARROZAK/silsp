package com.lsptddi.silsp.controller;

import org.springframework.stereotype.Controller;

import com.lsptddi.silsp.dto.RegisterDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import com.lsptddi.silsp.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.Optional;

import java.security.Principal;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

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

    @Autowired
    private TokenResetPasswordRepository tokenRepository;
    @Autowired
    private EmailService emailService;

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

    @GetMapping("/switch-role")
    public String switchRolePage(Model model, Principal principal, Authentication authentication) {
        if (principal == null) {
            return "redirect:/login"; // Kick jika belum login
        }

        // Ambil Data User untuk ditampilkan namanya
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("user", user);

        // Ambil List Role yang dimiliki user saat ini (untuk logika tombol di HTML)
        // Kita kirim sebagai Set<String> agar mudah dicek di Thymeleaf
        // Contoh output: ["Asesi", "Asesor"]
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        model.addAttribute("userRoles", roles);

        return "switch-role"; // Pastikan nama file html sama
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

    // ==========================================
    // 1. HALAMAN LUPA PASSWORD (GET)
    // ==========================================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<?> processForgotPassword(@RequestParam("email") String email, HttpServletRequest request) {
        // 1. Cari user by Email
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // FITUR BARU: HAPUS TOKEN LAMA JIKA ADA (Agar bisa request berkali-kali)
            Optional<TokenResetPassword> oldToken = tokenRepository.findByUser(user);
            if (oldToken.isPresent()) {
                tokenRepository.delete(oldToken.get());
            }

            // 2. Buat Token Baru
            String token = UUID.randomUUID().toString();
            TokenResetPassword myToken = new TokenResetPassword(token, user);
            tokenRepository.save(myToken);

            // 3. Buat Link & Kirim Email
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String resetLink = appUrl + "/reset-password?token=" + token;

            String subject = "Permintaan Reset Password - SILSP";
            String body = "Halo " + user.getFullName() + ",\n\n" +
                    "Kami menerima permintaan untuk mereset kata sandi akun Anda.\n" +
                    "Silakan klik tautan di bawah ini untuk membuat kata sandi baru:\n" +
                    resetLink + "\n\n\n" +

                    "Admin\n" +
                    "LSP Teknologi Data Digital Indonesia";

            emailService.sendEmail(user.getEmail(), subject, body);
        }

        // Response sukses (selalu sukses demi keamanan, tapi email hanya terkirim jika
        // user valid)
        return ResponseEntity.ok()
                .body("{\"status\":\"success\", \"message\":\"Tautan reset password telah dikirim ke email Anda.\"}");
    }

    // // ==========================================
    // // 2. PROSES KIRIM EMAIL (POST)
    // // ==========================================
    // @PostMapping("/forgot-password")
    // @ResponseBody // Kita pakai AJAX agar UX bagus
    // public ResponseEntity<?> processForgotPassword(@RequestParam String email,
    // HttpServletRequest request) {
    // // Cari user by Email
    // User user = userRepository.findByEmail(email).orElse(null);

    // // SECURITY BEST PRACTICE:
    // // Jika user tidak ditemukan, TETAP return sukses.
    // // Jangan beri tahu hacker bahwa "Email tidak terdaftar".
    // // Tapi kirim email hanya jika user ada.

    // if (user != null) {
    // // 1. Buat Token Unik (UUID)
    // String token = UUID.randomUUID().toString();

    // // 2. Simpan Token ke Database
    // TokenResetPassword myToken = new TokenResetPassword(token, user);
    // tokenRepository.save(myToken);

    // // 3. Buat Link Reset
    // String appUrl = request.getScheme() + "://" + request.getServerName() + ":" +
    // request.getServerPort();
    // String resetLink = appUrl + "/reset-password?token=" + token;

    // // 4. Kirim Email

    // }

    // return ResponseEntity.ok()
    // .body("{\"status\":\"success\", \"message\":\"Jika email terdaftar, tautan
    // reset telah dikirim.\"}");
    // }

    // ==========================================
    // 3. HALAMAN RESET PASSWORD (GET - DARI LINK EMAIL)
    // ==========================================
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {
        // Validasi Token
        TokenResetPassword passToken = tokenRepository.findByToken(token).orElse(null);

        if (passToken == null || passToken.isExpired()) {
            model.addAttribute("error", "Token tidak valid atau sudah kedaluwarsa.");
            return "forgot-password"; // Balik ke halaman lupa password
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // ==========================================
    // 4. PROSES SIMPAN PASSWORD BARU (POST)
    // ==========================================
    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<?> processResetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password) {

        TokenResetPassword passToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalid"));

        if (passToken.isExpired()) {
            return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Token sudah kedaluwarsa!\"}");
        }

        // Update Password User
        User user = passToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // Hapus Token (Agar tidak bisa dipakai lagi - Super Aman)
        tokenRepository.delete(passToken);

        return ResponseEntity.ok().body("{\"status\":\"success\", \"message\":\"Kata sandi berhasil diubah!\"}");
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
