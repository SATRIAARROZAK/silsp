package com.lsptddi.silsp.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    
    /**
     * Menampilkan halaman login kustom.
     * @return String nama template login (login.html)
     */
    @GetMapping("/login")
    public String loginPage() {
        // Mengembalikan nama file HTML dari folder 'templates'
        return "login";
    }

    /**
     * (Opsional) Mengarahkan halaman root ("/") ke halaman login.
     * @return String redirect ke /login
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
    
}