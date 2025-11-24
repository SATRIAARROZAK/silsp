package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.dto.UserDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
// ... imports lain ...

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private RefEducationRepository educationRepository; // Tambahan
    @Autowired private RefJobTypeRepository jobTypeRepository; // Tambahan
    // @Autowired private PasswordEncoder passwordEncoder; // Jika ada

    // ... method list users ...

    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserDto userDto) {
        User user = new User();
        
        // 1. Mapping Data Biasa
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
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
        
        // 2. Mapping Data Detail Pekerjaan
        user.setCompanyName(userDto.getCompanyName());
        user.setPosition(userDto.getPosition());
        user.setOfficePhone(userDto.getOfficePhone());
        user.setOfficeEmail(userDto.getOfficeEmail());
        user.setOfficeFax(userDto.getOfficeFax());
        user.setOfficeAddress(userDto.getOfficeAddress());

        // 3. SET RELASI 3NF (Pendidikan & Pekerjaan)
        if (userDto.getEducationId() != null) {
            RefEducation edu = educationRepository.findById(userDto.getEducationId()).orElse(null);
            user.setEducationId(edu);
        }

        if (userDto.getJobTypeId() != null) {
            RefJobType job = jobTypeRepository.findById(userDto.getJobTypeId()).orElse(null);
            user.setJobTypeId(job);
        }

        // 4. SET WILAYAH (Simpan ID-nya saja)
        user.setProvinceId(userDto.getProvinceId());
        user.setCityId(userDto.getCityId());
        user.setDistrictId(userDto.getDistrictId());
        user.setSubDistrictId(userDto.getSubDistrictId());

        // 5. SET TANDA TANGAN
        user.setSignatureBase64(userDto.getSignatureBase64());

        // Simpan
        userRepository.save(user);
        return "redirect:/admin/users?success";
    }
}