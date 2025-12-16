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

        // Jika hanya 1 Role, lakukan auto-redirect seperti biasa
        String redirectUrl = null;
        if (roles.contains("Admin")) {
            redirectUrl = "/admin/dashboard";
        } else if (roles.contains("Asesi")) {
            redirectUrl = "/asesi";
        } else if (roles.contains("Asesor")) {
            redirectUrl = "/asesor";
        } else if (roles.contains("Direktur")) {
            redirectUrl = "/admin/dashboard";
        } else {
            throw new IllegalStateException("Role tidak dikenali");
        }

        response.sendRedirect(redirectUrl);
    }

}