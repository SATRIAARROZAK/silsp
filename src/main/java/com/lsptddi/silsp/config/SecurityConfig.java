package com.lsptddi.silsp.config;

import com.lsptddi.silsp.security.CustomLoginSuccessHandler;
import com.lsptddi.silsp.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

        @Bean // Membuat sebuah "Bean" yang akan dikelola oleh Spring
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // // Menonaktifkan CSRF protection (untuk saat ini, agar API test mudah)
                                // .csrf(AbstractHttpConfigurer::disable)
                                .csrf(csrf -> csrf.disable())

                                // .authenticationProvider(authenticationProvider()) // PENTING!
                                .authorizeHttpRequests((requests) -> requests
                                                // Halaman yang boleh diakses SIAPA SAJA (Tanpa Login)
                                                .requestMatchers("/dev/**").permitAll()
                                                .requestMatchers("/login", "/register", "/reset-password",
                                                                "/forgot-password", "/assets/**", "/plugins/**",
                                                                "/dist/**")
                                                .permitAll()

                                                // IZINKAN AKSES KE SWITCH ROLE BAGI YANG SUDAH LOGIN
                                                .requestMatchers("/switch-role").authenticated()

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

                                // .authenticationProvider(authenticationProvider()) //
                                .formLogin((form) -> form
                                                .loginPage("/login") // URL halaman login Anda
                                                .loginProcessingUrl("/login") // URL post form (Spring Security
                                                                              // menangkap ini)
                                                .successHandler(successHandler) // <--- GUNAKAN HANDLER KITA DISINI
                                                // .failureHandler(failureHandler) // <--- GUNAKAN CUSTOM FAILURE
                                                // HANDLER
                                                .permitAll())
                                .logout((logout) -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                .logoutSuccessUrl("/login")
                                                .permitAll());
                                // 4. FITUR CEGAH UNDO/BACK (Disable Cache)
                                // .headers(headers -> headers
                                //                 .cacheControl(cache -> cache.disable()));

                return http.build();
        }

}