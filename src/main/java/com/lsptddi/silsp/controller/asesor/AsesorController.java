package com.lsptddi.silsp.controller.asesor;

import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/asesor")
public class AsesorController {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            model.addAttribute("user", user);
        }
    }

    @GetMapping("/dashboard")
    public String index() {
        return "pages/asesor/dashboard";
    }

    @GetMapping("/surat-tugas")
    public String showSertifikasiList() {
        return "pages/admin/surat/surat-tugas-list"; // Sesuaikan path html-nya
    }
}