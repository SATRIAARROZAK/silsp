package com.lsptddi.silsp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

        // Ambil semua role user saat login sukses
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 2. JIKA ROLE > 1, ARAHKAN KE PEMILIHAN ROLE
        if (roles.size() > 1) {
            // Simpan semua role asli ke session untuk keamanan/referensi nanti (Opsional)
            HttpSession session = request.getSession();
            session.setAttribute("ORIGINAL_ROLES", roles);

            response.sendRedirect("/switch-role");
            return;
        }

        // 1. JIKA ROLE HANYA 1, LANGSUNG ARAHKAN
        String redirectUrl = determineTargetUrl(roles);
        response.sendRedirect(redirectUrl);
    }

    // Helper untuk menentukan URL berdasarkan Role
    private String determineTargetUrl(Set<String> roles) {
        if (roles.contains("Admin"))
            return "/admin/dashboard";
        if (roles.contains("Asesi"))
            return "/asesi/dashboard";
        if (roles.contains("Asesor"))
            return "/asesor/dashboard";
        if (roles.contains("Direktur"))
            return "/direktur/dashboard";
        return "/login";
    }

}
