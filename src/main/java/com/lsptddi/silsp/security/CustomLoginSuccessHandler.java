package com.lsptddi.silsp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // LOGIKA BARU: Jika user punya LEBIH DARI 1 Role, arahkan ke Switch Role Page
        if (roles.size() > 1) {
            response.sendRedirect("/switch-role");
            return;
        }

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
        }

        // Default jika tidak ada role yang cocok (Atau user biasa)
        if (redirectUrl == null) {
            redirectUrl = "/home"; // Halaman umum

        }

        // Lakukan Redirect
        response.sendRedirect(redirectUrl);
    }

}