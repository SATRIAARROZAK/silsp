package com.lsptddi.silsp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Ambil list role user yang baru login
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Logic Redirect
        String redirectUrl = null;

        for (GrantedAuthority authority : authorities) {
            // Jika role-nya ADMIN
            if (authority.getAuthority().equals("Admin")) {
                redirectUrl = "/admin/dashboard";
                break;
            }
            // Jika role-nya ASESI
            else if (authority.getAuthority().equals("Asesi")) {
                redirectUrl = "/asesi/dashboard"; // Sesuaikan jika punya halaman khusus
                break;
            }
            // Jika role-nya ASESOR
            else if (authority.getAuthority().equals("Asesor")) {
                redirectUrl = "/asesor/dashboard";
                break;
            }
            // --- TAMBAHAN BARU ---
            else if (authority.getAuthority().equals("Direktur")) {
                redirectUrl = "/direktur/dashboard"; // Atau "/admin/dashboard" jika sama
                break;
            }

            // --- UBAH BAGIAN BAWAH INI ---

            // Jangan response.sendRedirect(redirectUrl);
            // Tapi kirim JSON agar JS yang melakukan redirect

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            Map<String, String> data = new HashMap<>();
            data.put("status", "success");
            data.put("redirectUrl", redirectUrl);

            response.getWriter().write(objectMapper.writeValueAsString(data));
        }

        // Default jika tidak ada role yang cocok (Atau user biasa)
        if (redirectUrl == null) {
            redirectUrl = "/home"; // Halaman umum

        }

        // Lakukan Redirect
        response.sendRedirect(redirectUrl);
    }
}