package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.dto.UserDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder; // Pastikan ada ini

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository; // <--- WAJIB DI-INJECT
    @Autowired private RefEducationRepository educationRepository;
    @Autowired private RefJobTypeRepository jobTypeRepository;
    
    // Jika Anda menggunakan Spring Security, inject PasswordEncoder
    @Autowired private PasswordEncoder passwordEncoder; 

    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserDto userDto) {
        User user = new User();

        // 1. DATA AKUN DASAR
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        
        // Password Default (Harusnya di-encode)
        user.setPassword(passwordEncoder.encode("12345678"));
        // user.setPassword(passwordEncoder.encode("123456")); 
        // user.setPassword("{noop}123456"); // Contoh tanpa encoder sementara

        // ==========================================
        // PERBAIKAN: LOGIKA SIMPAN ROLE
        // ==========================================
        Set<Role> roles = new HashSet<>();
        
        // Cek apakah user memilih role di form?
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            for (String roleName : userDto.getRoles()) {
                // Cari Role di database berdasarkan nama ("ADMIN", "ASESI", dll)
                Role role = roleRepository.findByName(roleName).orElse(null);
                
                if (role != null) {
                    roles.add(role);
                } else {
                    // Opsional: Handle jika role tidak ditemukan di DB
                    System.out.println("Role tidak ditemukan: " + roleName);
                }
            }
        }
        // Set Role ke Entity User
        user.setRoles(roles);
        // ==========================================


        // 2. DATA PRIBADI & LAINNYA (Sama seperti sebelumnya)
        user.setFullName(userDto.getFullName());
        user.setBirthPlace(userDto.getBirthPlace());
        user.setBirthDate(userDto.getBirthDate());
        user.setGender(userDto.getGender());
        user.setNik(userDto.getNik());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddress(userDto.getAddress());
        user.setPostalCode(userDto.getPostalCode());
        user.setCitizenship(userDto.getCitizenship());
        user.setNoMet(userDto.getNoMet());

        user.setCompanyName(userDto.getCompanyName());
        user.setPosition(userDto.getPosition());
        user.setOfficePhone(userDto.getOfficePhone());
        user.setOfficeEmail(userDto.getOfficeEmail());
        user.setOfficeFax(userDto.getOfficeFax());
        user.setOfficeAddress(userDto.getOfficeAddress());

        // 3. RELASI 3NF (ID ke Object)
        if (userDto.getEducationId() != null) {
            user.setEducationId(educationRepository.findById(userDto.getEducationId()).orElse(null));
        }
        if (userDto.getJobTypeId() != null) {
            user.setJobTypeId(jobTypeRepository.findById(userDto.getJobTypeId()).orElse(null));
        }

        // 4. WILAYAH & SIGNATURE
        user.setProvinceId(userDto.getProvinceId());
        user.setCityId(userDto.getCityId());
        user.setDistrictId(userDto.getDistrictId());
        user.setSubDistrictId(userDto.getSubDistrictId());
        user.setSignatureBase64(userDto.getSignatureBase64());

        userRepository.save(user);
        return "redirect:/admin/users?success";
    }
}