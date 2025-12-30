package com.lsptddi.silsp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();
        String activeRole = (String) session.getAttribute("activeRole");

        // 1. Jika belum pilih role (masih null) tapi coba akses halaman dalam
        // Kecuali halaman switch-role, logout, atau resource static
        if (activeRole == null) {
            // Izinkan akses ke public, login, switch-role, api, dan assets
            if (isPublicUrl(requestURI)) {
                return true;
            }
            // Jika memaksa masuk dashboard tanpa pilih role -> lempar ke switch-role
            response.sendRedirect("/switch-role");
            return false;
        }

        // 2. CEGAH URL MANIPULATION (Inti Keamanan)
        // Jika Active Role = "Asesi", tapi mau akses URL "/admin/..." -> BLOKIR
        
        if (requestURI.startsWith("/admin") && !activeRole.equals("Admin") && !activeRole.equals("Direktur")) {
            response.sendError(403, "Akses Ditolak: Anda sedang login sebagai " + activeRole);
            return false;
        }
        
        if (requestURI.startsWith("/asesi") && !activeRole.equals("Asesi")) {
            response.sendError(403, "Akses Ditolak: Anda sedang login sebagai " + activeRole);
            return false;
        }
        
        if (requestURI.startsWith("/asesor") && !activeRole.equals("Asesor")) {
            response.sendError(403, "Akses Ditolak: Anda sedang login sebagai " + activeRole);
            return false;
        }

        return true;
    }

    private boolean isPublicUrl(String uri) {
        return uri.startsWith("/login") || 
               uri.startsWith("/register") || 
               uri.startsWith("/switch-role") || 
               uri.startsWith("/select-role") || // Endpoint baru nanti
               uri.startsWith("/logout") ||
               uri.startsWith("/dist") || 
               uri.startsWith("/plugins") ||
               uri.equals("/error");
    }
}