package com.lsptddi.silsp.controller.direktur;


import com.lsptddi.silsp.model.User; // Import Model User Asli
import com.lsptddi.silsp.repository.UserRepository; // Import Repo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/direktur")
public class DirekturController {

     @Autowired
    private UserRepository userRepository;

    // Method ini akan dijalankan sebelum setiap request di controller ini
    // Fungsinya mengambil User asli dari database berdasarkan siapa yang login
    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName(); // Ambil username yang login
            User user = userRepository.findByUsername(username)
                    .orElse(null); // Cari di DB
            
            // Masukkan object User asli ke Thymeleaf
            // Jadi di HTML bisa panggil ${user.fullName}, ${user.email}, dll
            model.addAttribute("user", user); 
        }
    }

    @GetMapping("/dashboard")
    public String index() {
        return "pages/direktur/dashboard";
    }
    
}