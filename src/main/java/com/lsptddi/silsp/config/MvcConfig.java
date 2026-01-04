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
        // PERBAIKAN: Menggunakan toUri() agar path kompatibel di semua OS
        // (Windows/Linux)
        Path uploadDir = Paths.get("./uploads");
        String uploadPath = uploadDir.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Cache Control (Agar gambar baru langsung muncul tanpa cache lama)
        WebContentInterceptor cacheInterceptor = new WebContentInterceptor();
        cacheInterceptor.setCacheSeconds(0);
        registry.addInterceptor(cacheInterceptor);

        // Security Interceptor (Biarkan seperti sebelumnya)
        registry.addInterceptor(new RoleAccessInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/dist/**", "/plugins/**", "/login", "/register", "/api/**", "/uploads/**",
                        "/forgot-password", "/reset-password");
    }
}