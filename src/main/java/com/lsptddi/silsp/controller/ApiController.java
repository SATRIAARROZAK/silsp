package com.lsptddi.silsp.controller;

import com.lsptddi.silsp.model.KukSkema;
import com.lsptddi.silsp.model.PersyaratanSkema;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

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
    // // Logic untuk menandai semua sudah dibaca (Optional, bisa dikembangkan
    // nanti)
    // // Untuk sekarang biarkan count tetap ada atau reset saat dropdown dibuka
    // return ResponseEntity.ok().build();
    // }

    @PostMapping("/notifications/mark-read")
    public ResponseEntity<?> markAllRead(Principal principal) {
        if (principal == null)
            return ResponseEntity.ok().build();
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

    // 1. API: Ambil Jadwal berdasarkan ID Skema
    // Logic: Cari jadwal yang mengandung skema tersebut
    // @GetMapping("/jadwal-by-skema/{skemaId}")
    // public ResponseEntity<?> getJadwalBySkema(@PathVariable Long skemaId) {
    // // Cari semua jadwal, filter yang punya skemaId ini
    // // Note: Idealnya pake Custom Query JPQL agar performa bagus, tapi stream
    // juga
    // // oke untuk data sedikit
    // List<Schedule> allSchedules = scheduleRepository.findAll();

    // List<Map<String, Object>> filteredJadwal = allSchedules.stream()
    // .filter(s -> s.getSchemas().stream().anyMatch(ss ->
    // ss.getSchema().getId().equals(skemaId)))
    // .map(s -> {
    // Map<String, Object> map = new HashMap<>();
    // map.put("id", s.getId());
    // map.put("text", s.getName() + " - " + s.getTuk().getName() + " (" +
    // s.getStartDate() + ")");
    // return map;
    // })
    // .collect(Collectors.toList());

    // return ResponseEntity.ok(filteredJadwal);
    // }

    @GetMapping("/jadwal-by-skema/{skemaId}")
    public ResponseEntity<?> getJadwalBySkema(@PathVariable Long skemaId) {
        List<Schedule> allSchedules = scheduleRepository.findAll();

        // Ambil tanggal hari ini
        LocalDate today = LocalDate.now();

        List<Map<String, Object>> filteredJadwal = allSchedules.stream()
                .filter(s ->
                // 1. Cek kesesuaian Skema
                s.getSchemas().stream().anyMatch(ss -> ss.getSchema().getId().equals(skemaId)) &&
                // 2. Cek Tanggal: Harus SETELAH hari ini (Besok dst).
                // Jika hari ini tgl 21, jadwal tgl 21 tidak muncul.
                        s.getStartDate().isAfter(today))
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    // Format tampilan: Nama Jadwal - TUK (Tanggal)
                    map.put("text", s.getName() + " - " + s.getTuk().getName() + " (" + s.getStartDate() + ")");
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredJadwal);
    }

    // 2. API: Ambil Detail Jadwal (Untuk Sumber Anggaran & Pemberi Anggaran)
    // @GetMapping("/jadwal-detail/{id}")
    // public ResponseEntity<?> getJadwalDetail(@PathVariable Long id) {
    // Optional<Schedule> scheduleOpt = scheduleRepository.findById(id);
    // if (scheduleOpt.isPresent()) {
    // Schedule s = scheduleOpt.get();
    // Map<String, Object> map = new HashMap<>();
    // map.put("sumberAnggaran", s.getBudgetSource() != null ?
    // s.getBudgetSource().getName() : "-");
    // map.put("pemberiAnggaran", s.getBudgetProvider() != null ?
    // s.getBudgetProvider().getName() : "-");
    // return ResponseEntity.ok(map);
    // }
    // return ResponseEntity.notFound().build();
    // }

    @GetMapping("/jadwal-detail/{id}")
    public ResponseEntity<?> getJadwalDetail(@PathVariable Long id) {
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(id);
        if (scheduleOpt.isPresent()) {
            Schedule s = scheduleOpt.get();
            Map<String, Object> map = new HashMap<>();

            // Kirim Nama (untuk display)
            map.put("sumberAnggaranNama", s.getBudgetSource() != null ? s.getBudgetSource().getName() : "-");
            map.put("pemberiAnggaranNama", s.getBudgetProvider() != null ? s.getBudgetProvider().getName() : "-");

            // Kirim ID (untuk value hidden input) -> PERBAIKAN PENTING
            map.put("sumberAnggaranId", s.getBudgetSource() != null ? s.getBudgetSource().getId() : null);
            map.put("pemberiAnggaranId", s.getBudgetProvider() != null ? s.getBudgetProvider().getId() : null);

            return ResponseEntity.ok(map);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/skema-detail/{id}")
    public ResponseEntity<?> getSkemaDetail(@PathVariable Long id) {
        Optional<Skema> skemaOpt = skemaRepository.findById(id);
        if (skemaOpt.isPresent()) {
            Skema skema = skemaOpt.get();
            Map<String, Object> response = new HashMap<>();

            response.put("namaSkema", skema.getName());

            List<String> requirements = skema.getRequirements().stream()
                    .map(PersyaratanSkema::getDescription)
                    .collect(Collectors.toList());
            response.put("requirements", requirements);

            // Mapping Units -> Elements -> KUK
            List<Map<String, Object>> units = skema.getUnits().stream().map(unit -> {
                Map<String, Object> unitMap = new HashMap<>();
                unitMap.put("code", unit.getCode());
                unitMap.put("title", unit.getTitle());

                List<Map<String, Object>> elements = unit.getElements().stream().map(el -> {
                    Map<String, Object> elMap = new HashMap<>();
                    elMap.put("id", el.getId()); // PENTING: Kirim ID Elemen
                    elMap.put("no", el.getNoElemen());
                    elMap.put("name", el.getNamaElemen());

                    List<String> kuks = el.getKuks().stream()
                            .map(KukSkema::getNamaKuk)
                            .collect(Collectors.toList());
                    elMap.put("kuks", kuks);

                    return elMap;
                }).collect(Collectors.toList());

                unitMap.put("elements", elements);
                return unitMap;
            }).collect(Collectors.toList());

            response.put("units", units);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
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