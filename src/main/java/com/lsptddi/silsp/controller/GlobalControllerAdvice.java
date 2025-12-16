package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    /**
     * Method ini otomatis jalan di SEMUA halaman.
     * Fungsinya mengambil data user dari database berdasarkan siapa yang login,
     * lalu mengirimnya ke HTML sebagai variabel "loggedInUser".
     */
    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Cek apakah user sedang login (bukan anonymous)
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {

            String username = auth.getName();

            // Ambil data lengkap user dari database
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                // Masukkan object user ke Model global
                model.addAttribute("loggedInUser", user);

                // Ambil Role Pertama untuk ditampilkan (misal: "ADMIN")
                if (!user.getRoles().isEmpty()) {
                    model.addAttribute("currentRole", user.getRoles().iterator().next().getName());
                } else {
                    model.addAttribute("currentRole", "User");
                }
            }
        }
    }
}