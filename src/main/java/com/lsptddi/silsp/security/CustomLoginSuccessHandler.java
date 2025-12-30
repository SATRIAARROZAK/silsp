package com.lsptddi.silsp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Import Session
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

        // 1. Ambil Role User
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 2. KONDISI MULTI ROLE (Lebih dari 1)
        // Arahkan ke halaman pemilihan role, biarkan user memilih sendiri nanti
        if (roles.size() > 1) {
            response.sendRedirect("/switch-role");
            return;
        }

        // 3. KONDISI SINGLE ROLE (Hanya 1)
        // Kita harus "Auto-Select" role tersebut agar Interceptor tidak memblokir
        if (roles.size() == 1) {
            String role = roles.iterator().next(); // Ambil satu-satunya role

            // PENTING: Set Session 'activeRole' otomatis!
            HttpSession session = request.getSession();
            session.setAttribute("activeRole", role);

            // Tentukan Tujuan Redirect
            String redirectUrl = "";
            switch (role) {
                case "Admin":
                case "Direktur": // Direktur biasanya masuk dashboard admin/khusus
                    redirectUrl = "/admin/dashboard";
                    break;
                case "Asesi":
                    redirectUrl = "/asesi/dashboard"; // Pastikan ada /dashboard jika mapping controller anda
                                                      // menggunakannya
                    break;
                case "Asesor":
                    redirectUrl = "/asesor/dashboard";
                    break;
                default:
                    // Fallback jika role tidak dikenali (cegah error loop)
                    session.removeAttribute("activeRole");
                    redirectUrl = "/login?error=unknown_role";
                    break;
            }

            response.sendRedirect(redirectUrl);
            return;
        }

        // Jika user tidak punya role sama sekali (Kasus jarang)
        response.sendRedirect("/login?error=no_role");
    }
}