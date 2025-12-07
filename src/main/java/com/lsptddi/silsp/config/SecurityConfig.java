package com.lsptddi.silsp.config;

import com.lsptddi.silsp.security.CustomAuthenticationFailureHandler;
import com.lsptddi.silsp.security.CustomLoginSuccessHandler;
import com.lsptddi.silsp.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private CustomLoginSuccessHandler successHandler; // Inject Handler Kita
        @Autowired
        private CustomAuthenticationFailureHandler failureHandler;

        @Bean
        public static PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);

                // PENTING: Set userDetailsService di sini
                authenticationManagerBuilder
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder());

                return authenticationManagerBuilder.build();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                // PENTING: Set ini ke false agar kita bisa membedakan error Username vs
                // Password
                authProvider.setHideUserNotFoundExceptions(false);
                return authProvider;
        }

        @Bean // Membuat sebuah "Bean" yang akan dikelola oleh Spring
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // // Menonaktifkan CSRF protection (untuk saat ini, agar API test mudah)
                                // .csrf(AbstractHttpConfigurer::disable)
                                // .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests((requests) -> requests
                                                // Halaman yang boleh diakses SIAPA SAJA (Tanpa Login)
                                                .requestMatchers("/dev/**").permitAll()
                                                .requestMatchers("/login", "/register", "/assets/**", "/plugins/**",
                                                                "/dist/**")
                                                .permitAll()

                                                // Halaman khusus ADMIN
                                                .requestMatchers("/admin/**").hasAuthority("Admin")

                                                // Halaman khusus ASESI (Contoh)
                                                .requestMatchers("/asesi/**").hasAuthority("Asesi")

                                                // Halaman khusus ASESI (Contoh)
                                                .requestMatchers("/asesor/**").hasAuthority("Asesor")

                                                // .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "DIREKTUR") //
                                                // Jika
                                                // // Direktur
                                                // // boleh
                                                // // akses
                                                // // halaman
                                                // // Admin
                                                // // ATAU
                                                .requestMatchers("/direktur/**").hasAuthority("Direktur") // Jika punya
                                                                                                          // halaman
                                                                                                          // sendiri
                                                // ...

                                                .anyRequest().authenticated())

                                .authenticationProvider(authenticationProvider()) //
                                .formLogin((form) -> form
                                                .loginPage("/login") // URL halaman login Anda
                                                .loginProcessingUrl("/login") // URL post form (Spring Security
                                                                              // menangkap ini)
                                                .successHandler(successHandler) // <--- GUNAKAN HANDLER KITA DISINI
                                                .failureHandler(failureHandler) // <--- GUNAKAN CUSTOM FAILURE HANDLER
                                                .permitAll())
                                .logout((logout) -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                // // Sisanya harus login

                // // // Aturan otorisasi request
                // .authorizeHttpRequests(auth -> auth
                // // // // Mengizinkan SEMUA request ke URL manapun tanpa perlu login
                // .requestMatchers("/admin/**").permitAll()
                // // // .anyRequest().permitAll());

                // // .authorizeHttpRequests(authorize -> authorize
                // .requestMatchers(
                // "/login",
                // "/register",

                // "/plugins/**",
                // // "/assets/**" // <-- TAMBAHKAN BARIS INI
                // // "/admin/**",
                // "/dist/**",
                // "/asesor/**",
                // "/asesi/**")
                // .permitAll()
                // .requestMatchers("/admin/**").authenticated() // Mengamankan semua halaman
                // admin
                // .anyRequest().authenticated())
                // .formLogin(form -> form
                // .loginPage("/login") // Mengarahkan ke halaman login kustom Anda
                // .loginProcessingUrl("/login") // Endpoint yang diproses Spring Security
                // .defaultSuccessUrl("/admin/dashboard", true) // Halaman setelah login sukses
                // .permitAll());

                return http.build();
        }

}
