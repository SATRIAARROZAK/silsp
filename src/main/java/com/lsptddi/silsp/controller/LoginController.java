// package com.lsptddi.silsp.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;

// @Controller
// public class LoginController {

//     /**
//      * Menampilkan halaman login kustom.
//      * 
//      * @return String nama template login (login.html)
//      */
//     @GetMapping("/login")
//     public String loginPage() {
//         // Mengembalikan nama file HTML dari folder 'templates'
//         return "login";
//     }

//     // --- TAMBAHKAN METODE INI ---
//     /**
//      * Menampilkan halaman registrasi kustom.
//      * 
//      * @return String nama template (register-custom.html)
//      */
//     @GetMapping("/register")
//     public String registerPage() {
//         return "register";
//     }
//     // ----------------------------

//     /**
//      * (Opsional) Mengarahkan halaman root ("/") ke halaman login.
//      * 
//      * @return String redirect ke /login
//      */
//     @GetMapping("/")
//     public String root() {
//         return "redirect:/login";
//     }

// }

package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.service.AuthService; // Import service baru
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; 
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // File HTML register Anda
    }

    // --- PROSES REGISTRASI ---
    @PostMapping("/register")
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Panggil service untuk simpan data sebagai Asesi
            authService.registerAsesi(username, email, password);
            
            // Jika sukses, kirim pesan sukses dan arahkan ke login
            redirectAttributes.addFlashAttribute("successMessage", "Registrasi berhasil! Silakan login.");
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            // Jika gagal (misal username kembar), kembali ke form register dengan error
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
}