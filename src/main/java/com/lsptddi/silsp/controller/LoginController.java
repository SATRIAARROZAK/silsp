package com.lsptddi.silsp.controller;

import org.springframework.stereotype.Controller;

import com.lsptddi.silsp.dto.RegisterDto;
import com.lsptddi.silsp.model.Role;
import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
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
    public String registerPage(Model model) {
        // Load data dropdown jika diperlukan (Province, JobType, Education)
        // model.addAttribute("jobs", jobTypeRepository.findAll());
        // model.addAttribute("educations", educationRepository.findAll());
        return "register"; // Mengarah ke register.html
    }
    // ----------------------------

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> processRegister(@ModelAttribute RegisterDto dto) {
        try {
            // 1. Validasi Duplikat
            if (userRepository.existsByUsername(dto.getUsername())) {
                return ResponseEntity.badRequest().body(
                        "{\"status\": \"error\", \"field\": \"username\", \"message\": \"Username sudah digunakan!\"}");
            }
            if (userRepository.existsByEmail(dto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("{\"status\": \"error\", \"field\": \"email\", \"message\": \"Email sudah terdaftar!\"}");
            }

            // 2. Buat User Entity
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setFullName(dto.getFullName());
            user.setNik(dto.getNik());
            user.setPhoneNumber(dto.getPhoneNumber());

            // ... Mapping field lainnya (Alamat, Lahir, dll) sesuai DTO ...
            // user.setAddress(dto.getAddress()); ... dst

            // 3. Logic Role & Data Khusus
            Role userRole = null;
            if ("Asesor".equals(dto.getRole())) {
                userRole = roleRepository.findByName("Asesor").orElse(null);
                user.setNoMet(dto.getNoMet()); // Simpan No MET
            } else {
                userRole = roleRepository.findByName("Asesi").orElse(null);
                // Simpan Data Asesi (Pekerjaan, Pendidikan, dll)
                user.setCompanyName(dto.getCompanyName());
                // ... dst
            }

            if (userRole != null) {
                user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            }

            // 4. Simpan
            userRepository.save(user);

            return ResponseEntity.ok()
                    .body("{\"status\": \"success\", \"message\": \"Registrasi berhasil! Silakan Login.\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\": \"error\", \"message\": \"Terjadi kesalahan server.\"}");
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