// package com.lsptddi.silsp.controller;

// import com.lsptddi.silsp.dto.UserDto;
// import com.lsptddi.silsp.model.*;
// import com.lsptddi.silsp.repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.security.crypto.password.PasswordEncoder; // Pastikan ada ini

// import java.util.HashSet;
// import java.util.Set;

// @Controller
// @RequestMapping("/admin/users")
// public class UserController {

//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private RoleRepository roleRepository; // <--- WAJIB DI-INJECT
//     @Autowired
//     private RefEducationRepository educationRepository;
//     @Autowired
//     private RefJobTypeRepository jobTypeRepository;
    
//     // Jika Anda menggunakan Spring Security, inject PasswordEncoder
//     @Autowired
//     private PasswordEncoder passwordEncoder;

    

//     // // ... imports

//     // @PostMapping("/update")
//     // @ResponseBody
//     // public ResponseEntity<?> updateUser(@ModelAttribute UserDto userDto) {
        
//     //     // 1. Cari User Lama di Database
//     //     User user = userRepository.findById(userDto.getId())
//     //             .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

//     //     // 2. Update Data Dasar
//     //     user.setUsername(userDto.getUsername());
//     //     user.setEmail(userDto.getEmail());
        
//     //     // Logic Password: Hanya ubah jika user mengisi password baru
//     //     // Jika kosong, biarkan password lama
//     //     // if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
//     //     //      user.setPassword(passwordEncoder.encode(userDto.getPassword()));
//     //     // }

//     //     // 3. Update Data Pribadi
//     //     user.setFullName(userDto.getFullName());
//     //     user.setBirthPlace(userDto.getBirthPlace());
//     //     user.setBirthDate(userDto.getBirthDate());
//     //     user.setGender(userDto.getGender());
//     //     user.setNik(userDto.getNik());
//     //     user.setPhoneNumber(userDto.getPhoneNumber());
//     //     user.setAddress(userDto.getAddress());
//     //     user.setPostalCode(userDto.getPostalCode());
//     //     user.setCitizenship(userDto.getCitizenship());
//     //     user.setNoMet(userDto.getNoMet());

//     //     // 4. Update Role (Reset lalu isi ulang)
//     //     Set<Role> roles = new HashSet<>();
//     //     if (userDto.getRoles() != null) {
//     //         for (String roleName : userDto.getRoles()) {
//     //             roleRepository.findByName(roleName).ifPresent(roles::add);
//     //         }
//     //     }
//     //     user.setRoles(roles);

//     //     // 5. Update Relasi 3NF & Detail Pekerjaan
//     //     if (userDto.getEducationId() != null) {
//     //         user.setEducationId(educationRepository.findById(userDto.getEducationId()).orElse(null));
//     //     }
//     //     if (userDto.getJobTypeId() != null) {
//     //         user.setJobTypeId(jobTypeRepository.findById(userDto.getJobTypeId()).orElse(null));
//     //     }
        
//     //     user.setCompanyName(userDto.getCompanyName());
//     //     user.setPosition(userDto.getPosition());
//     //     user.setOfficePhone(userDto.getOfficePhone());
//     //     user.setOfficeEmail(userDto.getOfficeEmail());
//     //     user.setOfficeFax(userDto.getOfficeFax());
//     //     user.setOfficeAddress(userDto.getOfficeAddress());

//     //     // 6. Update Wilayah
//     //     user.setProvinceId(userDto.getProvinceId());
//     //     user.setCityId(userDto.getCityId());
//     //     user.setDistrictId(userDto.getDistrictId());
//     //     user.setSubDistrictId(userDto.getSubDistrictId());

//     //     // 7. Update Tanda Tangan (Hanya jika ada tanda tangan baru)
//     //     if (userDto.getSignatureBase64() != null && !userDto.getSignatureBase64().isEmpty()) {
//     //         user.setSignatureBase64(userDto.getSignatureBase64());
//     //     }

//     //     userRepository.save(user);

//     //     return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Data pengguna berhasil diperbarui\"}");
//     // }
// }