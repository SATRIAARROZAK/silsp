package com.lsptddi.silsp.controller.asesor;

// DATA TRANSFER OBJECK
import com.lsptddi.silsp.dto.UserProfileDto;
// MODEL (DATABASE)
import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.SuratTugas;
import com.lsptddi.silsp.model.User;

// REPOSITORY
import com.lsptddi.silsp.repository.UserRepository;
import com.lsptddi.silsp.repository.SuratTugasRepository;
import com.lsptddi.silsp.repository.ScheduleRepository;

// UMUM
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import java.security.Principal;

@Controller
@RequestMapping("/asesor")
public class AsesorController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SuratTugasRepository suratTugasRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
            model.addAttribute("currentRole", "Asesor");
        }
    }

    @GetMapping("/dashboard")
    public String index() {
        return "pages/asesor/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Mapping ke DTO untuk ditampilkan di form
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        // dto.setNoTelp(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setBirthPlace(user.getBirthPlace());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setNik(user.getNik());
        dto.setPostalCode(user.getPostalCode());

        // 2. NO TELP: Hapus '0' di depan agar di form jadi (811...)
        if (user.getPhoneNumber() != null && user.getPhoneNumber().startsWith("0")) {
            dto.setPhoneNumber(user.getPhoneNumber().substring(1));
        } else {
            dto.setPhoneNumber(user.getPhoneNumber());
        }

        // Mapping Relasi ID (3NF)
        if (user.getEducationId() != null)
            dto.setEducationId(user.getEducationId().getId());
        if (user.getJobTypeId() != null)
            dto.setJobTypeId(user.getJobTypeId().getId());
        // 3. NO MET: Hapus "MET." di depan
        if (user.getNoMet() != null) {
            dto.setNoMet(user.getNoMet().replace("MET.", "").trim());
        }

        dto.setCompanyName(user.getCompanyName());
        dto.setPosition(user.getPosition());
        dto.setOfficePhone(user.getOfficePhone());
        dto.setOfficeEmail(user.getOfficeEmail());
        dto.setOfficeFax(user.getOfficeFax());
        dto.setOfficeAddress(user.getOfficeAddress());

        // dto.setAvatar(user.getAvatar()); // Removed: setAvatar expects MultipartFile,
        // not String
        dto.setSignatureBase64(user.getSignatureBase64());

        dto.setProvinceId(user.getProvinceId());
        dto.setCityId(user.getCityId());
        dto.setDistrictId(user.getDistrictId());

        model.addAttribute("userDto", dto);
        return "pages/asesor/asesor-profile"; // Mengarah ke file HTML shared
    }

    // @GetMapping("/jadwal")
    // public String showJadwalUji() {
    // return "pages/asesor/jadwal/jadwal-list"; // Sesuaikan path html-nya
    // }

    // ==========================================
    // LIST JADWAL ASESMEN (PERSONAL)
    // ==========================================
    @GetMapping("/jadwal") // Ubah URL agar lebih relevan
    public String showJadwalUji(Model model, Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        // 1. Ambil Username Asesor yang login
        String username = principal.getName();

        // Ambil User object untuk keperluan view (cek ID nanti)
        User currentUser = userRepository.findByUsername(username).orElse(null);

        // 2. Setup Pagination (Urutkan dari Jadwal Terbaru)
        // Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());

        // 3. Cari Surat Tugas milik Asesor ini
        Page<Schedule> pageJadwal = scheduleRepository.findJadwalByAsesor(username, keyword, pageable);

        // 4. Kirim ke View
        model.addAttribute("listJadwal", pageJadwal.getContent());
        model.addAttribute("currentUser", currentUser); // Kirim user untuk helper cek surat
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageJadwal.getTotalPages());
        model.addAttribute("totalItems", pageJadwal.getTotalElements());
        model.addAttribute("size", size);

        // Arahkan ke file HTML yang Anda upload
        return "pages/asesor/jadwal/jadwal-list";
    }

}