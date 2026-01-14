package com.lsptddi.silsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Gunakan @Controller, bukan @RestController
public class DashboardController {

    // Jika ada yang mengakses URL root "/", jalankan method ini
    @GetMapping("/test-dashboard")
    public String showDashboard(Model model) {
        // 1. Menyiapkan data yang akan dikirim ke tampilan
        String pesan = "Hello ini dari Backend (Monolitik)";

        // 2. "Membungkus" data ke dalam objek Model
        // "pesanDariServer" adalah nama variabel yang akan digunakan di HTML
        model.addAttribute("pesanDariServer", pesan);

        // 3. Mengembalikan nama file HTML yang ada di folder templates
        // Tidak perlu menulis .html, cukup namanya saja.
        return "dashboard";
    }
}

// Solusi&Langkah-Langkah Untuk mewujudkan

// keinginan Anda (menghilangkan /admin, /asesor, dll), Anda harus melakukan 4
// langkah perubahan secara serentak.
// 1. Perbaiki Controller (Mapping URL)

// Anda harus menghapus @RequestMapping("/admin") di atas class, dan memastikan
// URL unik.

// Contoh pada AdminController.java:

// @Controller
// // @RequestMapping("/admin") <--- HAPUS ATAU KOMENTAR BARIS INI
// public class AdminController {

// // Ganti mapping agar unik dan jelas
// // Dari "/data-asesor" (karena induknya admin hilang) menjadi "/data-asesor"
// langsung
// @GetMapping("/data-asesor")
// public String showDataAsesor(...) { ... }

// // HATI-HATI DENGAN DASHBOARD
// // Anda harus spesifik, misal:
// @GetMapping("/dashboard-admin")
// public String dashboardAdmin() { ... }
// }

// Contoh pada AsesorController.java:

// @Controller
// // @RequestMapping("/asesor") <--- HAPUS INI
// public class AsesorController {

// @GetMapping("/jadwal-asesmen") // Akses: localhost:8080/jadwal-asesmen
// public String showJadwal(...) { ... }

// // Dashboard harus beda nama URL-nya dengan admin
// @GetMapping("/dashboard-asesor")
// public String dashboardAsesor() { ... }
// }

// 2. Perbaiki SecurityConfig.java (Sangat Penting)

// Karena prefix /admin hilang, Anda tidak bisa lagi memblokir akses menggunakan
// .requestMatchers("/admin/**"). Anda harus mendaftar URL-nya satu per satu
// atau menggunakan pola tertentu.

// File: SecurityConfig.java

// .authorizeHttpRequests((requests) -> requests
// .requestMatchers("/", "/login", "/register", "/dist/**").permitAll()

// // --- IZIN UNTUK ADMIN ---
// // Anda harus sebutkan satu-satu URL-nya, atau gunakan pola nama file
// .requestMatchers(
// "/dashboard-admin",
// "/data-pengguna/**",
// "/data-asesi/**",
// "/data-asesor/**",
// "/jadwal-sertifikasi/**",
// "/surat-tugas-asesor/**"
// ).hasAuthority("Admin")

// // --- IZIN UNTUK ASESOR ---
// .requestMatchers(
// "/dashboard-asesor",
// "/jadwal-asesmen/**",
// "/profile-asesor/**"
// ).hasAuthority("Asesor")

// .anyRequest().authenticated()
// )

// 3. Perbaiki Sidebar & HTML (th:href)

// Semua link di sidebar.html, tombol di tabel, dan form th:action harus diedit
// manual.

// Contoh di sidebar.html:

// <a th:href="@{/admin/data-asesor}" class="nav-link">

// <a th:href="@{/data-asesor}" class="nav-link">

// Contoh di AdminController.java (Bagian Redirect):

// // SEBELUMNYA
// return "redirect:/admin/data-asesor";

// // SESUDAHNYA
// return "redirect:/data-asesor";

// 4. Solusi Khusus untuk Dashboard (Single Dashboard URL)

// Jika Anda bersikeras ingin URL-nya tetap bersih localhost:8080/dashboard
// untuk

// SEMUA user (baik Admin maupun Asesor), Anda harus membuat Satu Controller
// Pusat yang membagi arahnya.

// Buat controller baru, misal DashboardController.java:

// @Controller
// public class DashboardController {

// @GetMapping("/dashboard")
// public String showDashboard(Authentication auth) {
// String role = auth.getAuthorities().iterator().next().getAuthority();

// if (role.equals("Admin")) {
// return "pages/admin/dashboard"; // Arahkan ke file HTML admin
// } else if (role.equals("Asesor")) {
// return "pages/asesor/dashboard"; // Arahkan ke file HTML asesor
// } else if (role.equals("Asesi")) {
// return "pages/asesi/dashboard";
// }

// return "redirect:/login";
// }
// }

// Dengan cara poin 4 ini, URL di browser tetap localhost:8080/dashboard, tapi
// isinya berubah sesuai siapa yang login.
// Kesimpulan

// Bisa dilakukan? Bisa. Apakah saya sarankan? Jika project masih tahap awal,
// silakan. Tapi jika project sudah besar, risikonya adalah ada link yang lupa

// diubah (broken link) atau celah keamanan karena lupa mendaftarkan URL di
// SecurityConfig.

// Jika Anda ingin melanjutkan, silakan pilih Poin 4 (Single Dashboard
// Controller) agar URL dashboard terlihat rapi, dan hapus prefix di controller
// lain untuk menu-menu spesifik (/data-asesor, /jadwal, dll).