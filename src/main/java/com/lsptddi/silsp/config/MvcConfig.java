package com.lsptddi.silsp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;


import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose folder 'uploads' agar bisa diakses via URL /uploads/**
        Path uploadDir = Paths.get("./uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 1. INTERCEPTOR CACHE CONTROL (BARU)
        // Mencegah browser menyimpan halaman (No-Cache)
        WebContentInterceptor cacheInterceptor = new WebContentInterceptor();
        cacheInterceptor.setCacheSeconds(0);
        // cacheInterceptor.setUseExpiresHeader(true);
        // cacheInterceptor.setUseCacheControlHeader(true);
        // cacheInterceptor.setUseCacheControlNoStore(true);

        // Terapkan ke semua URL
        registry.addInterceptor(cacheInterceptor);
        // Daftarkan Logic Pengaman Role
        registry.addInterceptor(new RoleAccessInterceptor())
                .addPathPatterns("/**") // Cek semua URL
                .excludePathPatterns("/dist/**", "/plugins/**", "/login", "/register", "/api/**", "/uploads/**","/forgot-password", "/reset-password"); // Kecuali aset &
                                                                                                   // public
    }
}