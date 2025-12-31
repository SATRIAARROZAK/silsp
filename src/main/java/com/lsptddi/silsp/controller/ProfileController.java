// package com.lsptddi.silsp.controller;

// import com.lsptddi.silsp.dto.UserProfileDto;
// import com.lsptddi.silsp.model.User;
// import com.lsptddi.silsp.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.ResponseBody;

// import java.security.Principal;

// @Controller
// public class ProfileController {

//     @Autowired private UserRepository userRepository;
//     @Autowired private PasswordEncoder passwordEncoder;

//     @PostMapping("/profile/update")
//     @ResponseBody
//     public ResponseEntity<?> updateProfile(@ModelAttribute UserProfileDto dto, Principal principal) {
//         try {
//             User user = userRepository.findByUsername(principal.getName())
//                     .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

//             // 1. Validasi Password Lama (Jika user ingin ganti password)
//             if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
//                 // Cek password lama
//                 if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
//                     return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Password lama salah!\"}");
//                 }
//                 // Cek konfirmasi
//                 if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
//                     return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Konfirmasi password tidak cocok!\"}");
//                 }
//                 // Update Password
//                 user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
//             }

//             // 2. Update Data Diri
//             user.setFullName(dto.getFullName());
//             user.setEmail(dto.getEmail());
//             user.setPhoneNumber(dto.getNoTelp());
//             user.setAddress(dto.getAddress());
//             user.setProvinceId(dto.getProvinceId());
//             user.setCityId(dto.getCityId());
//             user.setDistrictId(dto.getDistrictId());
//             user.setNik(dto.getNik());
//             user.setGender(dto.getGender());
//             user.setBirthPlace(dto.getBirthPlace());
//             user.setBirthDate(dto.getBirthDate());

//             // ... Tambahkan field lain sesuai kebutuhan

//             // 3. Update Tanda Tangan (Jika ada perubahan)
//             if (dto.getSignatureBase64() != null && !dto.getSignatureBase64().isEmpty()) {
//                 user.setSignatureBase64(dto.getSignatureBase64());
//             }

//             userRepository.save(user);

//             return ResponseEntity.ok().body("{\"status\":\"success\", \"message\":\"Profil berhasil diperbarui!\"}");

//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.badRequest().body("{\"status\":\"error\", \"message\":\"Gagal update: " + e.getMessage() + "\"}");
//         }
//     }
// }

package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.lsptddi.silsp.dto.UserRoleDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lokasi penyimpanan foto (Pastikan folder ini ada atau terbuat otomatis)
    // Di production, gunakan path absolut atau cloud storage (S3/GCS)
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/avatars/";

    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@ModelAttribute UserProfileDto dto, Principal principal,
            RedirectAttributes redirectAttributes, RedirectView redirectView, UserRoleDto roleDto,
            @RequestParam("roles") List<String> roles) {
        try {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // 1. UPDATE DATA DIRI
            user.setFullName(dto.getFullName());
            user.setEmail(dto.getEmail());
            // user.setPhoneNumber(dto.getNoTelp());
            user.setAddress(dto.getAddress());
            user.setBirthPlace(dto.getBirthPlace());
            user.setBirthDate(dto.getBirthDate());
            user.setGender(dto.getGender());
            // user.setNik(dto.getNik());

            // Update Wilayah (Pastikan handle null jika user tidak mengubah wilayah)
            if (dto.getProvinceId() != null)
                user.setProvinceId(dto.getProvinceId());
            if (dto.getCityId() != null)
                user.setCityId(dto.getCityId());
            if (dto.getDistrictId() != null)
                user.setDistrictId(dto.getDistrictId());
            user.setPostalCode(dto.getPostalCode());

            // Data Asesi/Asesor khusus
            if (dto.getCitizenship() != null)
                user.setCitizenship(dto.getCitizenship());
            if (dto.getNoMet() != null)
                user.setNoMet(dto.getNoMet());

            // 2. LOGIKA GANTI PASSWORD
            if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
                // Validasi Password Lama
                if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                    return ResponseEntity.badRequest()
                            .body("{\"status\":\"error\", \"message\":\"Password lama salah!\"}");
                }
                // Validasi Konfirmasi
                if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                    return ResponseEntity.badRequest()
                            .body("{\"status\":\"error\", \"message\":\"Konfirmasi password tidak cocok!\"}");
                }
                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }

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

            if (roles.contains("Asesor")) {
                if (dto.getNoMet() != null) {
                    user.setNoMet("MET." + dto.getNoMet());
                }
            } else {
                // Jika bukan Asesor, bersihkan No MET
                user.setNoMet(null);
            }

            // 3. LOGIKA UPLOAD FOTO (AVATAR)
            MultipartFile avatarFile = dto.getAvatar();
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(avatarFile.getOriginalFilename()));
                // Generate nama unik agar tidak bentrok (misal: username_timestamp.jpg)
                String uniqueFileName = user.getUsername() + "_" + System.currentTimeMillis() + "_" + fileName;

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = avatarFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Simpan path relatif ke database agar bisa diakses browser
                    // Pastikan mapping resource handler di MvcConfig sudah benar
                    user.setAvatar("/uploads/avatars/" + uniqueFileName);
                } catch (IOException ioe) {
                    return ResponseEntity.badRequest().body(
                            "{\"status\":\"error\", \"message\":\"Gagal upload foto: " + ioe.getMessage() + "\"}");
                }
            }

            // 4. LOGIKA TANDA TANGAN (Base64)
            if (dto.getSignatureBase64() != null && !dto.getSignatureBase64().isEmpty()) {
                user.setSignatureBase64(dto.getSignatureBase64());
            }

            user.setCompanyName(dto.getCompanyName());
            user.setPosition(dto.getPosition());
            user.setOfficePhone(dto.getOfficePhone());
            user.setOfficeEmail(dto.getOfficeEmail());
            user.setOfficeFax(dto.getOfficeFax());
            user.setOfficeAddress(dto.getOfficeAddress());

            userRepository.save(user);

            return ResponseEntity.ok().body("{\"status\":\"success\", \"message\":\"Profil berhasil diperbarui!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("{\"status\":\"error\", \"message\":\"Terjadi kesalahan sistem: " + e.getMessage() + "\"}");
        }
    }
}