package com.lsptddi.silsp.controller;

// model
import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.ScheduleAssessor;
import com.lsptddi.silsp.model.Skema;
import com.lsptddi.silsp.model.User;

// repository
import com.lsptddi.silsp.repository.SkemaRepository;
import com.lsptddi.silsp.repository.ScheduleRepository;
import com.lsptddi.silsp.repository.SuratTugasRepository;
import com.lsptddi.silsp.repository.UserRepository;
import com.lsptddi.silsp.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private SkemaRepository skemaRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private SuratTugasRepository suratTugasRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    
    // API GET NOTIFIKASI USER
    @GetMapping("/notifications/unread")
    public ResponseEntity<?> getUnreadNotifications(Principal principal) {
        if (principal == null)
            return ResponseEntity.ok().build();

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null)
            return ResponseEntity.ok().build();

        Map<String, Object> response = new HashMap<>();

        // Hitung Jumlah Belum Baca
        Long count = notificationRepository.countUnread(user.getId());

        // Ambil 5 Notifikasi Terakhir
        // (Bisa pakai Pageable limit 5 agar tidak berat)
        // Disini saya ambil semua lalu stream limit 5 untuk simpel
        List<Map<String, Object>> notifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().limit(5).map(n -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", n.getId());
                    map.put("title", n.getTitle());
                    map.put("message", n.getMessage());
                    map.put("url", n.getTargetUrl());
                    map.put("isRead", n.isRead());
                    // Hitung waktu (misal: "2 menit yang lalu") bisa dihandle JS atau Java.
                    // Kirim raw date dulu
                    map.put("time", n.getCreatedAt().toString());
                    return map;
                }).collect(Collectors.toList());

        response.put("count", count);
        response.put("data", notifs);

        return ResponseEntity.ok(response);
    }

    // API MARK AS READ (Saat lonceng diklik)
    // @PostMapping("/notifications/mark-read")
    // public ResponseEntity<?> markAllRead(Principal principal) {
    //     // Logic untuk menandai semua sudah dibaca (Optional, bisa dikembangkan nanti)
    //     // Untuk sekarang biarkan count tetap ada atau reset saat dropdown dibuka
    //     return ResponseEntity.ok().build();
    // }

    @PostMapping("/notifications/mark-read")
    public ResponseEntity<?> markAllRead(Principal principal) {
        if (principal == null) return ResponseEntity.ok().build();
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        
        if (user != null) {
            notificationRepository.markAllAsRead(user.getId());
        }
        return ResponseEntity.ok().build();
    }

    // API MARK ONE READ (Saat satu item diklik) - Opsional tapi bagus
    @PostMapping("/notifications/mark-read/{id}")
    public ResponseEntity<?> markOneRead(@PathVariable Long id) {
        notificationRepository.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // try {
    // // Ambil data mentah dari BNSP dan teruskan ke Frontend
    // Object response = restTemplate.getForObject(url, Object.class);
    // return ResponseEntity.ok(response);
    // } catch (Exception e) {
    // return ResponseEntity.internalServerError().body("Gagal mengambil data
    // BNSP");
    // }
    // }

    // // --- 2. PROXY API BNSP (KEMENTRIAN) ---
    // @GetMapping("/proxy/kementrian")
    // public ResponseEntity<?> getKementrian() {
    // String url = "https://konstruksi.bnsp.go.id/api/v1/master/kementrian";
    // try {
    // Object response = restTemplate.getForObject(url, Object.class);
    // return ResponseEntity.ok(response);
    // } catch (Exception e) {
    // return ResponseEntity.internalServerError().body("Gagal mengambil data
    // BNSP");
    // }
    // }

    // --- 3. INTERNAL API (UNIT SKEMA) ---
    // @GetMapping("/internal/skema/{id}/units")
    // public ResponseEntity<?> getSkemaUnits(@PathVariable Long id) {
    // Skema skema = skemaRepository.findById(id).orElse(null);
    // if (skema == null)
    // return ResponseEntity.notFound().build();

    // // Mengembalikan list unit yang berelasi dengan skema tersebut
    // return ResponseEntity.ok(skema.getUnits());
    // }

    // @GetMapping("/internal/schedule/{id}/details")
    // @ResponseBody
    // public ResponseEntity<?> getScheduleDetails(@PathVariable Long id) {
    // System.out.println("--- API REQUEST: Get Schedule Details ID: " + id + "
    // ---");

    // // 1. Gunakan Query Eager Fetch yang baru dibuat
    // Schedule schedule = scheduleRepository.findByIdWithDetails(id).orElse(null);

    // if (schedule == null) {
    // System.out.println("Error: Jadwal tidak ditemukan di Database");
    // return ResponseEntity.notFound().build();
    // }

    // // 2. Siapkan List Asesor
    // List<Map<String, Object>> availableAssessors = new ArrayList<>();

    // // Cek null safety
    // if (schedule.getAssessors() != null && !schedule.getAssessors().isEmpty()) {
    // System.out.println("Jadwal punya " + schedule.getAssessors().size() + "
    // asesor.");

    // for (ScheduleAssessor sa : schedule.getAssessors()) {
    // User asesor = sa.getAsesor();

    // // Cek Database: Sudah ada surat belum?
    // boolean alreadyHasLetter =
    // suratTugasRepository.existsByJadwalAndAsesor(schedule, asesor);

    // if (!alreadyHasLetter) {
    // Map<String, Object> mapAsesor = new HashMap<>();
    // mapAsesor.put("id", asesor.getId());
    // mapAsesor.put("fullName", asesor.getFullName());
    // mapAsesor.put("noMet", asesor.getNoMet() != null ? asesor.getNoMet() : "-");
    // availableAssessors.add(mapAsesor);
    // } else {
    // System.out.println("Skipped Asesor: " + asesor.getFullName() + " (Sudah ada
    // surat)");
    // }
    // }
    // } else {
    // System.out.println("Warning: List Assessors di Jadwal ini KOSONG/NULL");
    // }

    // // 3. Siapkan Response
    // Map<String, Object> response = new HashMap<>();

    // // TUK
    // response.put("tukName", schedule.getTuk() != null ?
    // schedule.getTuk().getName() : "-");

    // // Tanggal
    // DateTimeFormatter indoFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy",
    // new Locale("id", "ID"));
    // response.put("tanggalPelaksanaan",
    // schedule.getStartDate().format(indoFormat));

    // // Skema
    // String skemaName = "-";
    // if (schedule.getSchemas() != null && !schedule.getSchemas().isEmpty()) {
    // skemaName = schedule.getSchemas().get(0).getSchema().getName();
    // }
    // response.put("skemaName", skemaName);

    // // Asesor List
    // response.put("assessors", availableAssessors);

    // System.out.println("Mengirim Response JSON ke Frontend...");
    // return ResponseEntity.ok(response);
    // }
}