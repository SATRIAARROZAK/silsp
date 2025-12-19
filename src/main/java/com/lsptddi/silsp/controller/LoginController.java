package com.lsptddi.silsp.controller;

import org.springframework.stereotype.Controller;

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

import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;

@Controller
public class LoginController {
    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenResetPasswordRepository tokenRepository;
    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    // MENCEGAH USER LOGGED-IN MENGAKSES HALAMAN LOGIN
    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Jika sudah login, redirect sesuai role yang aktif saat ini
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

            // Logika redirect sederhana jika user nekat akses /login
            if (roles.contains("Admin"))
                return "redirect:/admin/dashboard";
            if (roles.contains("Asesi"))
                return "redirect:/asesi/dashboard";
            if (roles.contains("Asesor"))
                return "redirect:/asesor/dashboard";
            if (roles.contains("Direktur"))
                return "redirect:/direktur/dashboard";

            return "redirect:/switch-role";
        }
        return "login";
    }

    // HALAMAN PILIH ROLE
    @GetMapping("/switch-role")
    public String switchRolePage(Model model, Authentication authentication) {
        if (authentication == null)
            return "redirect:/login";

        // Kirim list role yang dimiliki user ke HTML
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        model.addAttribute("userRoles", roles);
        // Ambil nama user (Principal biasanya berisi UserDetails atau username string)
        model.addAttribute("username", authentication.getName());

        return "switch-role";
    }

    // 3. LOGIKA ISOLASI ROLE (CORE FEATURE)
    @GetMapping("/auth/select-role")
    public String selectRole(@RequestParam String targetRole, Authentication authentication) {
        if (authentication == null)
            return "redirect:/login";

        // A. VERIFIKASI KEAMANAN
        // Cek apakah user BENAR-BENAR punya role yang dipilih dalam list
        // Authorities-nya saat ini?
        boolean hasRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(targetRole));

        if (!hasRole) {
            // Jika mencoba akses role yang tidak dimiliki -> Kick / Error
            return "redirect:/switch-role?error=unauthorized";
        }

        // B. MODIFIKASI SECURITY CONTEXT (ISOLASI)
        // Kita buat list authority baru yang HANYA berisi role yang dipilih
        List<GrantedAuthority> newAuthorities = new ArrayList<>();
        newAuthorities.add(new SimpleGrantedAuthority(targetRole));

        // Buat token autentikasi baru dengan authority tunggal
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                newAuthorities // Hanya role terpilih yang dimasukkan
        );

        // Set ke Context Holder (Global Session)
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // C. REDIRECT KE TUJUAN
        if (targetRole.equals("Admin"))
            return "redirect:/admin/dashboard";
        if (targetRole.equals("Asesi"))
            return "redirect:/asesi/dashboard";
        if (targetRole.equals("Asesor"))
            return "redirect:/asesor/dashboard";
        if (targetRole.equals("Direktur"))
            return "redirect:/direktur/dashboard";

        return "redirect:/";
    }

    // @GetMapping("/register")
    // public String registerPage() {
    // return "register";
    // }

    // // 2. API CEK DUPLIKAT (Untuk Validasi Realtime JS)
    // @GetMapping("/api/check-duplicate")
    // @ResponseBody
    // public ResponseEntity<?> checkDuplicate(@RequestParam(required = false)
    // String username,
    // @RequestParam(required = false) String email) {
    // if (username != null && userRepository.existsByUsername(username)) {
    // return ResponseEntity.ok(false); // Ada duplikat -> Invalid
    // }
    // if (email != null && userRepository.existsByEmail(email)) {
    // return ResponseEntity.ok(false); // Ada duplikat -> Invalid
    // }
    // return ResponseEntity.ok(true); // Aman -> Valid
    // }

    // // 3. PROSES REGISTRASI (POST)
    // @PostMapping("/register")
    // @ResponseBody
    // public ResponseEntity<?> registerProcess(@ModelAttribute RegisterDto dto) {
    // try {
    // // 1. VALIDASI DATA DARI DTO
    // // Pastikan Role tidak null
    // if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
    // return ResponseEntity.badRequest().body("{\"status\":\"error\",
    // \"message\":\"Role wajib dipilih!\"}");
    // }

    // // Validasi Duplikat (Server Side Protection)
    // if (userRepository.existsByUsername(dto.getUsername())) {
    // return ResponseEntity.badRequest()
    // .body("{\"status\":\"error\", \"message\":\"Username sudah digunakan!\"}");
    // }
    // if (userRepository.existsByEmail(dto.getEmail())) {
    // return ResponseEntity.badRequest()
    // .body("{\"status\":\"error\", \"message\":\"Email sudah digunakan!\"}");
    // }

    // // 2. SETUP USER BARU
    // User user = new User();
    // user.setUsername(dto.getUsername());
    // user.setEmail(dto.getEmail());
    // user.setPassword(passwordEncoder.encode(dto.getPassword())); // Enkripsi

    // // ------------------------------------------------------------------
    // // PERBAIKAN UTAMA: ROLE SENSITIVITY
    // // ------------------------------------------------------------------
    // // Hapus .toUpperCase(). Kita gunakan input mentah dari HTML
    // ("Asesi"/"Asesor")
    // // Pastikan di database tabel 'roles' isinya: "Asesi" dan "Asesor" (bukan
    // // ASESI/ASESOR)
    // String roleName = dto.getRoles();

    // Role userRole = roleRepository.findByName(roleName)
    // .orElseThrow(() -> new RuntimeException("Role '" + roleName
    // + "' tidak ditemukan di Database. Pastikan Master Data Role sudah diisi
    // dengan benar (Asesi/Asesor)."));

    // user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
    // // ------------------------------------------------------------------

    // // 3. MAPPING DATA PRIBADI
    // user.setFullName(dto.getFullName());
    // user.setNik(dto.getNik());
    // user.setBirthPlace(dto.getBirthPlace());
    // user.setBirthDate(dto.getBirthDate());
    // user.setGender(dto.getGender());
    // user.setCitizenship(dto.getCitizenship());
    // user.setPhoneNumber(dto.getPhoneNumber());

    // // 4. KHUSUS ASESOR (NO MET)
    // if ("Asesor".equals(roleName) && dto.getNoMet() != null) {
    // // Format: MET + spasi + input
    // user.setNoMet("MET. " + dto.getNoMet());
    // }

    // // 5. RELASI (PENDIDIKAN & PEKERJAAN)
    // if (dto.getEducationId() != null) {
    // // Ambil object Education dari ID (Pastikan Repository Education
    // di-autowired)
    // //
    // user.setLastEducation(educationRepository.findById(dto.getEducationId()).orElse(null));
    // }
    // // Logic Job Type (Khusus Asesor)
    // if ("Asesor".equals(roleName) && dto.getJobTypeId() != null) {
    // //
    // user.setJobType(jobTypeRepository.findById(dto.getJobTypeId()).orElse(null));
    // }

    // // 6. WILAYAH & ALAMAT
    // user.setProvinceId(dto.getProvinceId());
    // user.setCityId(dto.getCityId());
    // user.setDistrictId(dto.getDistrictId());
    // // user.setSubDistrictId(dto.getSubDistrictId());
    // user.setPostalCode(dto.getPostalCode());
    // user.setAddress(dto.getAddress());

    // // 7. TANDA TANGAN
    // user.setSignatureBase64(dto.getSignatureBase64());

    // // SIMPAN KE DATABASE
    // userRepository.save(user);

    // return ResponseEntity.ok()
    // .body("{\"status\":\"success\", \"message\":\"Registrasi Berhasil! Silakan
    // Login.\"}");

    // } catch (Exception e) {
    // e.printStackTrace(); // Cek console server untuk detail error
    // return ResponseEntity.badRequest()
    // .body("{\"status\":\"error\", \"message\":\"Gagal Mendaftar: " +
    // e.getMessage() + "\"}");
    // }
    // }

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
}
