package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.UserRepository;
import com.lsptddi.silsp.security.CustomLoginSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class DevHelperController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomLoginSuccessHandler successHandler;

    /**
     * MAGIC LINK UNTUK DEV
     * Contoh akses: http://localhost:8080/dev/login?user=admin
     */
    @GetMapping("/dev/login")
    public void autoLogin(@RequestParam String user, HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        
        // 1. Cari User berdasarkan username (tanpa cek password)
        User userDb = userRepository.findByUsername(user).orElse(null);

        if (userDb == null) {
            response.getWriter().write("User '" + user + "' tidak ditemukan di database!");
            return;
        }

        // 2. Buat Token Autentikasi Spring Security (Pura-pura sudah login sukses)
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
                userDb.getUsername(),
                userDb.getPassword(),
                userDb.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );

        // 3. Masukkan ke Security Context
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authReq);

        // 4. Simpan ke Session (PENTING: Agar saat refresh halaman, login tetap nempel)
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        // 5. Redirect otomatis sesuai Role (Menggunakan Handler yang sudah kita buat)
        successHandler.onAuthenticationSuccess(request, response, authReq);
    }
}