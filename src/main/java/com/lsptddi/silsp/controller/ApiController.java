// package com.lsptddi.silsp.controller;

// import com.lsptddi.silsp.model.Schedule;
// import com.lsptddi.silsp.model.ScheduleAssessor;
// import com.lsptddi.silsp.model.Skema;
// import com.lsptddi.silsp.model.User;
// import com.lsptddi.silsp.repository.SkemaRepository;
// import com.lsptddi.silsp.repository.ScheduleRepository;
// import com.lsptddi.silsp.repository.SuratTugasRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.client.RestTemplate;

// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Locale;
// import java.util.Map;

// @RestController
// @RequestMapping("/api")
// public class ApiController {

//     @Autowired
//     private SkemaRepository skemaRepository;
//     @Autowired
//     private RestTemplate restTemplate;
//     @Autowired
//     private ScheduleRepository scheduleRepository;
//     @Autowired
//     private SuratTugasRepository suratTugasRepository;

//     // try {
//     // // Ambil data mentah dari BNSP dan teruskan ke Frontend
//     // Object response = restTemplate.getForObject(url, Object.class);
//     // return ResponseEntity.ok(response);
//     // } catch (Exception e) {
//     // return ResponseEntity.internalServerError().body("Gagal mengambil data
//     // BNSP");
//     // }
//     // }

//     // // --- 2. PROXY API BNSP (KEMENTRIAN) ---
//     // @GetMapping("/proxy/kementrian")
//     // public ResponseEntity<?> getKementrian() {
//     // String url = "https://konstruksi.bnsp.go.id/api/v1/master/kementrian";
//     // try {
//     // Object response = restTemplate.getForObject(url, Object.class);
//     // return ResponseEntity.ok(response);
//     // } catch (Exception e) {
//     // return ResponseEntity.internalServerError().body("Gagal mengambil data
//     // BNSP");
//     // }
//     // }

//     // --- 3. INTERNAL API (UNIT SKEMA) ---
//     @GetMapping("/internal/skema/{id}/units")
//     public ResponseEntity<?> getSkemaUnits(@PathVariable Long id) {
//         Skema skema = skemaRepository.findById(id).orElse(null);
//         if (skema == null)
//             return ResponseEntity.notFound().build();

//         // Mengembalikan list unit yang berelasi dengan skema tersebut
//         return ResponseEntity.ok(skema.getUnits());
//     }

//     @GetMapping("/internal/schedule/{id}/details")
//     @ResponseBody
//     public ResponseEntity<?> getScheduleDetails(@PathVariable Long id) {
//         System.out.println("--- API REQUEST: Get Schedule Details ID: " + id + " ---");

//         // 1. Gunakan Query Eager Fetch yang baru dibuat
//         Schedule schedule = scheduleRepository.findByIdWithDetails(id).orElse(null);

//         if (schedule == null) {
//             System.out.println("Error: Jadwal tidak ditemukan di Database");
//             return ResponseEntity.notFound().build();
//         }

//         // 2. Siapkan List Asesor
//         List<Map<String, Object>> availableAssessors = new ArrayList<>();

//         // Cek null safety
//         if (schedule.getAssessors() != null && !schedule.getAssessors().isEmpty()) {
//             System.out.println("Jadwal punya " + schedule.getAssessors().size() + " asesor.");

//             for (ScheduleAssessor sa : schedule.getAssessors()) {
//                 User asesor = sa.getAsesor();

//                 // Cek Database: Sudah ada surat belum?
//                 boolean alreadyHasLetter = suratTugasRepository.existsByJadwalAndAsesor(schedule, asesor);

//                 if (!alreadyHasLetter) {
//                     Map<String, Object> mapAsesor = new HashMap<>();
//                     mapAsesor.put("id", asesor.getId());
//                     mapAsesor.put("fullName", asesor.getFullName());
//                     mapAsesor.put("noMet", asesor.getNoMet() != null ? asesor.getNoMet() : "-");
//                     availableAssessors.add(mapAsesor);
//                 } else {
//                     System.out.println("Skipped Asesor: " + asesor.getFullName() + " (Sudah ada surat)");
//                 }
//             }
//         } else {
//             System.out.println("Warning: List Assessors di Jadwal ini KOSONG/NULL");
//         }

//         // 3. Siapkan Response
//         Map<String, Object> response = new HashMap<>();

//         // TUK
//         response.put("tukName", schedule.getTuk() != null ? schedule.getTuk().getName() : "-");

//         // Tanggal
//         DateTimeFormatter indoFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
//         response.put("tanggalPelaksanaan", schedule.getStartDate().format(indoFormat));

//         // Skema
//         String skemaName = "-";
//         if (schedule.getSchemas() != null && !schedule.getSchemas().isEmpty()) {
//             skemaName = schedule.getSchemas().get(0).getSchema().getName();
//         }
//         response.put("skemaName", skemaName);

//         // Asesor List
//         response.put("assessors", availableAssessors);

//         System.out.println("Mengirim Response JSON ke Frontend...");
//         return ResponseEntity.ok(response);
//     }
// }