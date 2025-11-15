package com.lsptddi.silsp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean // Membuat sebuah "Bean" yang akan dikelola oleh Spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Menonaktifkan CSRF protection (untuk saat ini, agar API test mudah)
                .csrf(AbstractHttpConfigurer::disable)
                // .csrf(csrf -> csrf.disable())
                // // Aturan otorisasi request
                .authorizeHttpRequests(auth -> auth
                //         // Mengizinkan SEMUA request ke URL manapun tanpa perlu login
                //         .requestMatchers("/admin/**").permitAll()
                //         .anyRequest().permitAll());

        // .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(
        "/login",
        "/dist/**",
        "/plugins/**"
        // "/assets/**" // <-- TAMBAHKAN BARIS INI
        // "/admin/**"
        ).permitAll()
        .requestMatchers("/admin/**").authenticated() // Mengamankan semua halaman admin
        .anyRequest().authenticated())
        .formLogin(form -> form
        .loginPage("/login") // Mengarahkan ke halaman login kustom Anda
        .loginProcessingUrl("/login") // Endpoint yang diproses Spring Security
        .defaultSuccessUrl("/admin/dashboard", true) // Halaman setelah login sukses
        .permitAll());

        return http.build();
    }

}