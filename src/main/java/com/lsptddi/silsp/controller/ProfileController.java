package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@ModelAttribute UserProfileDto dto, Principal principal) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // 1. Validasi Password Lama (Jika user ingin ganti password)
            if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
                // Cek password lama
                if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                    return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Password lama salah!\"}");
                }
                // Cek konfirmasi
                if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                    return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Konfirmasi password tidak cocok!\"}");
                }
                // Update Password
                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }

            // 2. Update Data Diri
            user.setFullName(dto.getFullName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getNoTelp());
            user.setAddress(dto.getAddress());
            user.setProvinceId(dto.getProvinceId());
            user.setCityId(dto.getCityId());
            user.setDistrictId(dto.getDistrictId());
            user.setNik(dto.getNik());
            user.setGender(dto.getGender());
            user.setBirthPlace(dto.getBirthPlace());
            user.setBirthDate(dto.getBirthDate());
            
            // ... Tambahkan field lain sesuai kebutuhan


            // 3. Update Tanda Tangan (Jika ada perubahan)
            if (dto.getSignatureBase64() != null && !dto.getSignatureBase64().isEmpty()) {
                user.setSignatureBase64(dto.getSignatureBase64());
            }

            userRepository.save(user);

            return ResponseEntity.ok().body("{\"status\":\"success\", \"message\":\"Profil berhasil diperbarui!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Gagal update: " + e.getMessage() + "\"}");
        }
    }
}