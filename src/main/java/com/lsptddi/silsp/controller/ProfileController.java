package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.Principal;
import java.util.Objects;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TypeEducationRepository educationRepository;
    @Autowired
    private TypePekerjaanRepository jobTypeRepository;

    // Path Upload (Sesuaikan dengan config resource handler Anda)
    private static final String UPLOAD_DIR = "uploads/avatars/";

    // ==========================================
    // 1. API CEK PASSWORD (REALTIME VALIDATION)
    // ==========================================
    @PostMapping("/api/check-current-password")
    @ResponseBody
    public ResponseEntity<?> checkCurrentPassword(@RequestParam String currentPassword, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());
        return ResponseEntity.ok(matches); // Return true jika cocok, false jika salah
    }

    // ==========================================
    // 2. UPDATE PROFILE (SEMUA DATA)
    // ==========================================
    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@ModelAttribute UserProfileDto dto, Principal principal) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // --- A. UPDATE DATA UMUM (Admin, Asesi, Asesor) ---
            user.setEmail(dto.getEmail()); // Email boleh diubah
            // Username TIDAK diupdate (Read-only)

            user.setFullName(dto.getFullName());
            // user.setPhoneNumber(dto.getPhoneNumber());
            user.setAddress(dto.getAddress());
            user.setBirthPlace(dto.getBirthPlace());
            user.setBirthDate(dto.getBirthDate());
            user.setGender(dto.getGender());
            // user.setNik(dto.getNik());

            // 1. NIK: Bersihkan titik lagi sebelum simpan update
            if (dto.getNik() != null) {
                user.setNik(dto.getNik().replaceAll("[^0-9]", ""));
            }

            // 2. NO TELP: Tambahkan '0' lagi
            if (dto.getPhoneNumber() != null) {
                String rawPhone = dto.getPhoneNumber().replaceAll("[^0-9]", "");
                if (rawPhone.startsWith("0"))
                    rawPhone = rawPhone.substring(1);
                user.setPhoneNumber("0" + rawPhone);
            }

            // Update Wilayah
            if (dto.getProvinceId() != null)
                user.setProvinceId(dto.getProvinceId());
            if (dto.getCityId() != null)
                user.setCityId(dto.getCityId());
            if (dto.getDistrictId() != null)
                user.setDistrictId(dto.getDistrictId());
            user.setPostalCode(dto.getPostalCode());

            // --- B. UPDATE DATA KHUSUS ---

            // Kewarganegaraan (Asesi/Asesor)
            if (dto.getCitizenship() != null)
                user.setCitizenship(dto.getCitizenship());

            // // No MET (Asesor)
            // if (dto.getNoMet() != null)
            //     user.setNoMet(dto.getNoMet());

            // Relasi Pendidikan
            if (dto.getEducationId() != null) {
                user.setEducationId(educationRepository.findById(dto.getEducationId()).orElse(null));
            }

            if (dto.getNoMet() != null) {
                user.setNoMet("MET." + dto.getNoMet());
            }
            // Relasi Pekerjaan (Asesi/Asesor)
            if (dto.getJobTypeId() != null) {
                user.setJobTypeId(jobTypeRepository.findById(dto.getJobTypeId()).orElse(null));
            }

            // Detail Pekerjaan (Asesi)
            user.setCompanyName(dto.getCompanyName());
            user.setPosition(dto.getPosition());
            user.setOfficePhone(dto.getOfficePhone());
            user.setOfficeEmail(dto.getOfficeEmail());
            user.setOfficeAddress(dto.getOfficeAddress());

            // --- C. GANTI PASSWORD ---
            if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
                if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                    return ResponseEntity.badRequest()
                            .body("{\"status\":\"error\", \"message\":\"Password lama salah!\"}");
                }
                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }

            // --- D. UPLOAD FOTO ---
            MultipartFile avatarFile = dto.getAvatar();
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(avatarFile.getOriginalFilename()));
                String uniqueFileName = user.getUsername() + "_" + System.currentTimeMillis() + ".jpg"; // Paksa .jpg
                                                                                                        // atau ambil
                                                                                                        // ext

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath))
                    Files.createDirectories(uploadPath);

                try (InputStream inputStream = avatarFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    // Simpan path yang bisa diakses web
                    user.setAvatar("/uploads/avatars/" + uniqueFileName);
                }
            }
            // PENTING: Jangan set avatar null jika user tidak upload file baru
            // Logic di atas hanya jalan IF file ada. Jika tidak, avatar lama tetap aman.

            // --- E. TANDA TANGAN ---
            if (dto.getSignatureBase64() != null && !dto.getSignatureBase64().isEmpty()) {
                user.setSignatureBase64(dto.getSignatureBase64());
            }

            userRepository.save(user);

            return ResponseEntity.ok().body("{\"status\":\"success\", \"message\":\"Profil berhasil diperbarui!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\":\"error\", \"message\":\"Gagal: " + e.getMessage() + "\"}");
        }
    }
}