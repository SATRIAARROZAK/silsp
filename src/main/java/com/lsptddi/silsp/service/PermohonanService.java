// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;
// import com.lsptddi.silsp.model.*;
// import com.lsptddi.silsp.repository.*;
// import jakarta.transaction.Transactional;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.nio.file.*;
// import java.util.*;

// @Service
// public class PermohonanService {

//     @Autowired
//     private PermohonanSertifikasiRepository permohonanRepository;
//     @Autowired
//     private BuktiPersyaratanRepository persyaratanRepository;
//     @Autowired
//     private BuktiAdministrasiRepository buktiAdminRepository;
//     @Autowired
//     private BuktiPortofolioRepository portofolioRepository;
//     @Autowired
//     private AsesmenMandiriRepository asesmenMandiriRepository;
//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private SkemaRepository skemaRepository;
//     @Autowired
//     private ScheduleRepository scheduleRepository;
//     @Autowired
//     private PersyaratanSkemaRepository persyaratanSkemaRepository;
//     // @Autowired
//     // private KukSkemaRepository kukSkemaRepository;
//     @Autowired
//     private TypePekerjaanRepository jobTypeRepository;
//     @Autowired
//     private TypeEducationRepository educationRepository;
//     @Autowired
//     private UnitElemenSkemaRepository unitElemenSkemaRepository;
//     @Autowired
//     private TypeSumberAnggaranRepository sumberAnggaranRepository;
//     @Autowired
//     private TypePemberiAnggaranRepository pemberiAnggaranRepository;

//     private final String UPLOAD_DIR = "uploads/permohonan/";

//     // @Transactional
//     // public void processPermohonan(
//     // User user,
//     // Long skemaId,
//     // Long jadwalId,
//     // String sumberAnggaran,
//     // String pemberiAnggaran,
//     // String tujuanAsesmen,
//     // UserProfileDto userUpdateData,
//     // Map<String, MultipartFile> files, // Semua file (Syarat, Admin, Portofolio)
//     // Map<String, String> apl02Data // Data K/BK dan link bukti (JSON string / Map
//     // keys)
//     // ) throws IOException {

//     @Transactional
//     public void processPermohonan(
//             User user,
//             Long skemaId,
//             Long jadwalId,
//             Long sumberAnggaranId, // Ubah parameter jadi Long ID
//             Long pemberiAnggaranId, // Ubah parameter jadi Long ID
//             String tujuanAsesmen,
//             UserProfileDto userUpdateData,
//             Map<String, MultipartFile> files,
//             Map<String, String> apl02Data, // Data K/BK
//             Map<String, String[]> apl02Bukti // Data Bukti (Array)
//     ) throws IOException {

//         // @Transactional
//         // public void processPermohonan(
//         // User user,
//         // Long skemaId,
//         // Long jadwalId,
//         // Long sumberAnggaranId, // Ubah parameter jadi Long ID
//         // Long pemberiAnggaranId, // Ubah parameter jadi Long ID
//         // String tujuanAsesmen,
//         // UserProfileDto userUpdateData,
//         // Map<String, MultipartFile> files,
//         // Map<String, String> apl02Data, // Data K/BK
//         // Map<String, String[]> apl02Bukti // Data Bukti (Array)
//         // ) throws IOException {

//         // 1. UPDATE DATA PEMOHON (USER PROFILE)
//         updateUserData(user, userUpdateData);

//         // 2. SIMPAN HEADER PERMOHONAN
//         PermohonanSertifikasi permohonan = new PermohonanSertifikasi();
//         permohonan.setAsesi(user);
//         permohonan.setSkema(skemaRepository.findById(skemaId).orElseThrow());
//         permohonan.setJadwal(scheduleRepository.findById(jadwalId).orElseThrow());
//         if (sumberAnggaranId != null)
//             permohonan.setSumberAnggaran(sumberAnggaranRepository.findById(sumberAnggaranId).orElse(null));
//         if (pemberiAnggaranId != null)
//             permohonan.setPemberiAnggaran(pemberiAnggaranRepository.findById(pemberiAnggaranId).orElse(null));
//         permohonan.setTujuanAsesmen(tujuanAsesmen);
//         permohonan = permohonanRepository.save(permohonan);

//         // 3. PROSES FILE & DATA TAB

//         // A. Tab 3: Persyaratan Dasar
//         // Key format dari JS: "syarat_{id_persyaratan_db}"
//         for (String key : files.keySet()) {
//             if (key.startsWith("syarat_")) {
//                 Long syaratId = Long.parseLong(key.split("_")[1]);
//                 String fileName = saveFile(files.get(key), user.getUsername() + "_syarat_" + syaratId);

//                 BuktiPersyaratan pp = new BuktiPersyaratan();
//                 pp.setPermohonan(permohonan);
//                 pp.setPersyaratanSkema(persyaratanSkemaRepository.findById(syaratId).orElse(null));
//                 pp.setFilePath(fileName);
//                 pp.setStatus("PENDING");
//                 persyaratanRepository.save(pp);
//             }
//         }

//         // B. Tab 4: Bukti Administrasi
//         // Key format: "administrasi_{tipe}" (1=KTP, 2=Foto)
//         for (String key : files.keySet()) {
//             if (key.startsWith("administrasi_")) {
//                 String tipe = key.split("_")[1]; // "1" or "2"
//                 String namaDok = tipe.equals("1") ? "KTP" : "Pas Foto";
//                 String fileName = saveFile(files.get(key), user.getUsername() + "_admin_" + tipe);

//                 BuktiAdministrasi ba = new BuktiAdministrasi();
//                 ba.setPermohonan(permohonan);
//                 ba.setJenisBukti(namaDok);
//                 ba.setFilePath(fileName);
//                 buktiAdminRepository.save(ba);
//             }
//         }

//         // C. Tab 5: Bukti Portofolio
//         // Key format: "portofolio_{temp_id}"
//         // Kita butuh Map untuk mapping TempID ke Entity Object agar bisa dipakai di
//         // APL-02
//         Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();

//         for (String key : files.keySet()) {
//             if (key.startsWith("portofolio_")) {
//                 String tempId = key; // e.g. "portofolio_1"
//                 MultipartFile file = files.get(key);
//                 String fileName = saveFile(file, user.getUsername() + "_" + tempId);

//                 BuktiPortofolio bp = new BuktiPortofolio();
//                 bp.setPermohonan(permohonan);
//                 bp.setNamaDokumen(file.getOriginalFilename());
//                 bp.setFilePath(fileName);
//                 bp.setTempId(tempId); // Simpan temp ID untuk lookup nanti

//                 portofolioRepository.save(bp);
//                 portofolioMap.put(tempId, bp); // Masukkan ke Map
//             }
//         }

//         // D. Tab 7: Bukti Kompetensi (APL-02)
//         // Data dikirim dari JS dalam bentuk key-value param
//         // Format Key Checkbox: "kompeten_{kukId}" -> Value "K" or "BK"
//         // Format Key Bukti: "bukti_{kukId}" -> Value "portofolio_1,portofolio_2" (comma
//         // separated)

//         for (String key : apl02Data.keySet()) {
//             if (key.startsWith("kompeten_elemen_")) {
//                 // Parsing Key: "kompeten_elemen_123"
//                 String[] parts = key.split("_");

//                 // Ambil ID dari index ke-2
//                 Long elemenId = Long.parseLong(parts[2]);
//                 String rekomendasi = apl02Data.get(key); // K atau BK

//                 AsesmenMandiri am = new AsesmenMandiri();
//                 am.setPermohonan(permohonan);

//                 // Set Relasi ke UnitElemenSkema (Bukan KUK)
//                 // Pastikan Anda sudah Inject UnitElemenSkemaRepository
//                 am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));

//                 am.setRekomendasiAsesi(rekomendasi);

//                 // Link Bukti (name="bukti_elemen_123")
//                 String buktiKey = "bukti_elemen_" + elemenId;
//                 if (apl02Bukti.containsKey(buktiKey)) {
//                     String[] buktiIds = apl02Bukti.get(buktiKey);
//                     List<BuktiPortofolio> buktiList = new ArrayList<>();
//                     if (buktiIds != null) {
//                         for (String bId : buktiIds) {
//                             // bId contoh: "portofolio_1" -> Cari di Map
//                             if (portofolioMap.containsKey(bId)) {
//                                 buktiList.add(portofolioMap.get(bId));
//                             }
//                         }
//                     }
//                     am.setBuktiRelevan(buktiList);
//                 }
//                 asesmenMandiriRepository.save(am);
//             }
//         }

//         // for (String key : apl02Data.keySet()) {
//         // if (key.startsWith("kompeten_elemen_")) {

//         // // Parsing ID Elemen (Aman karena split index 2)
//         // // "kompeten" "elemen" "12"
//         // String[] parts = key.split("_");
//         // Long elemenId = Long.parseLong(parts[2]);
//         // String rekomendasi = apl02Data.get(key); // "K" atau "BK"

//         // AsesmenMandiri am = new AsesmenMandiri();
//         // am.setPermohonan(permohonan);

//         // // Set Elemen (Bukan KUK lagi)
//         // am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));

//         // am.setRekomendasiAsesi(rekomendasi);

//         // // Link ke Bukti Relevan
//         // // Format key bukti: "bukti_elemen_{elemenId}"
//         // String buktiKey = "bukti_elemen_" + elemenId;

//         // if (apl02Bukti.containsKey(buktiKey)) {
//         // String[] buktiIds = apl02Bukti.get(buktiKey); // Dapat array dari select2
//         // List<BuktiPortofolio> buktiList = new ArrayList<>();

//         // if (buktiIds != null) {
//         // for (String bId : buktiIds) {
//         // // bId contoh: "portofolio_1"
//         // if (portofolioMap.containsKey(bId)) {
//         // buktiList.add(portofolioMap.get(bId));
//         // }
//         // }
//         // }
//         // am.setBuktiRelevan(buktiList);
//         // }

//         // asesmenMandiriRepository.save(am);
//         // }
//         // }
//     }

//     // for (String key : apl02Data.keySet()) {
//     // if (key.startsWith("kompeten_elemen_")) {
//     // try {
//     // // Parsing ID Elemen yang aman (Split string)
//     // // "kompeten" "elemen" "123"
//     // String[] parts = key.split("_");
//     // Long elemenId = Long.parseLong(parts[2]);
//     // String rekomendasi = apl02Data.get(key);

//     // AsesmenMandiri am = new AsesmenMandiri();
//     // am.setPermohonan(permohonan);

//     // // Set Unit Elemen (Bukan KUK lagi)
//     // UnitElemenSkema elem = elemenSkemaRepository.findById(elemenId).orElse(null);
//     // am.setUnitElemen(elem);

//     // am.setRekomendasiAsesi(rekomendasi);

//     // // Link Bukti (Many-to-Many)
//     // String buktiKey = "bukti_elemen_" + elemenId;
//     // if (apl02Bukti.containsKey(buktiKey)) {
//     // String[] buktiIds = apl02Bukti.get(buktiKey);
//     // List<BuktiPortofolio> listBukti = new ArrayList<>();
//     // if(buktiIds != null) {
//     // for(String bId : buktiIds) {
//     // if(portofolioMap.containsKey(bId)) {
//     // listBukti.add(portofolioMap.get(bId));
//     // }
//     // }
//     // }
//     // am.setBuktiRelevan(listBukti);
//     // }

//     // asesmenMandiriRepository.save(am);
//     // } catch (NumberFormatException e) {
//     // System.out.println("Gagal parsing ID Elemen dari key: " + key);
//     // }
//     // }
//     // }

//     private void updateUserData(User user, UserProfileDto dto) {
//         // Update field yang diperbolehkan

//         user.setFullName(dto.getFullName());
//         user.setBirthPlace(dto.getBirthPlace());
//         user.setBirthDate(dto.getBirthDate());
//         user.setGender(dto.getGender());
//         user.setEmail(dto.getEmail());
//         // user.setNoTelp(dto.getPhoneNumber());
//         user.setProvinceId(dto.getProvinceId());
//         user.setCityId(dto.getCityId());
//         user.setDistrictId(dto.getDistrictId());
//         user.setAddress(dto.getAddress());
//         user.setPostalCode(dto.getPostalCode());

//         // 2. NO TELP: Hapus '0' di depan agar di form jadi (811...)
//         if (dto.getPhoneNumber() != null) {
//             String rawPhone = dto.getPhoneNumber().replaceAll("[^0-9]", "");
//             if (rawPhone.startsWith("0"))
//                 rawPhone = rawPhone.substring(1);
//             user.setPhoneNumber("0" + rawPhone);
//         }

//         // 1. NIK: Bersihkan titik lagi sebelum simpan update
//         if (dto.getNik() != null) {
//             user.setNik(dto.getNik().replaceAll("[^0-9]", ""));
//         }
//         // Mapping Relasi ID (3NF)
//         // Mapping Relasi ID (3NF)
//         if (dto.getEducationId() != null)
//             user.setEducationId(educationRepository.findById(dto.getEducationId()).orElse(null));
//         if (dto.getJobTypeId() != null)
//             user.setJobTypeId(jobTypeRepository.findById(dto.getJobTypeId()).orElse(null));

//         user.setCompanyName(dto.getCompanyName());
//         user.setPosition(dto.getPosition());
//         user.setOfficePhone(dto.getOfficePhone());
//         user.setOfficeEmail(dto.getOfficeEmail());
//         user.setOfficeFax(dto.getOfficeFax());
//         user.setOfficeAddress(dto.getOfficeAddress());

//         // user.setNik(dto.getNik());
//         // user.setFullName(dto.getFullName());
//         // user.setBirthPlace(dto.getBirthPlace());
//         // user.setBirthDate(dto.getBirthDate());
//         // user.setGender(dto.getGender());
//         // user.setAddress(dto.getAddress());
//         // user.setPostalCode(dto.getPostalCode());
//         // user.setPhoneNumber(dto.getPhoneNumber()); // Pastikan format 08xx

//         // // Pekerjaan
//         // if (dto.getJobTypeId() != null)
//         // user.setJobTypeId(jobTypeRepository.findById(dto.getJobTypeId()).orElse(null));
//         // user.setCompanyName(dto.getCompanyName());
//         // user.setPosition(dto.getPosition());
//         // user.setOfficeAddress(dto.getOfficeAddress());
//         // user.setOfficePhone(dto.getOfficePhone());
//         // user.setOfficeEmail(dto.getOfficeEmail());
//         // user.setOfficeFax(dto.getOfficeFax());

//         userRepository.save(user);
//     }

//     private String saveFile(MultipartFile file, String prefix) throws IOException {
//         String originalName = file.getOriginalFilename();
//         String ext = originalName.substring(originalName.lastIndexOf("."));
//         String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

//         Path path = Paths.get(UPLOAD_DIR + fileName);
//         Files.createDirectories(path.getParent());
//         Files.write(path, file.getBytes());

//         return fileName;
//     }
// }

// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;
// import com.lsptddi.silsp.model.*;
// import com.lsptddi.silsp.repository.*;
// import jakarta.transaction.Transactional;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.File;
// import java.io.IOException;
// import java.nio.file.*;
// import java.util.*;
// import java.time.LocalDateTime;
// import java.util.*;

// @Service
// public class PermohonanService {

//     @Autowired
//     private PermohonanSertifikasiRepository permohonanRepository;
//     @Autowired
//     private BuktiPersyaratanRepository persyaratanRepository;
//     @Autowired
//     private BuktiAdministrasiRepository buktiAdminRepository;
//     @Autowired
//     private BuktiPortofolioRepository portofolioRepository;
//     @Autowired
//     private AsesmenMandiriRepository asesmenMandiriRepository;
//     @Autowired
//     private UserRepository userRepository;
//     @Autowired
//     private SkemaRepository skemaRepository;
//     @Autowired
//     private ScheduleRepository scheduleRepository;
//     @Autowired
//     private PersyaratanSkemaRepository persyaratanSkemaRepository;

//     @Autowired
//     private UnitElemenSkemaRepository unitElemenSkemaRepository;
//     @Autowired
//     private KukSkemaRepository kukSkemaRepository;
//     @Autowired
//     private TypePekerjaanRepository jobTypeRepository;
//     @Autowired
//     private TypeEducationRepository educationRepository;
//     @Autowired
//     private TypeSumberAnggaranRepository sumberAnggaranRepository;
//     @Autowired
//     private TypePemberiAnggaranRepository pemberiAnggaranRepository;

//     @Autowired
//     private BuktiPersyaratanRepository buktiPersyaratanRepository;

//     @Autowired
//     private BuktiAdministrasiRepository buktiAdministrasiRepository;

//     @Autowired
//     private BuktiPortofolioRepository buktiPortofolioRepository;

//     @Autowired
//     private UnitElemenSkemaRepository unitElemenRepository;

//     private final String UPLOAD_DIR = "uploads/permohonan/";

//     @Transactional
//     public void processPermohonan(
//             User user,
//             Long skemaId,
//             Long jadwalId,
//             Long sumberId,
//             Long pemberiId,
//             String tujuanAsesmen,
//             UserProfileDto userDto,
//             Map<String, MultipartFile> fileMap,
//             // Param Baru: List Data dari JSON
//             List<Map<String, Object>> apl02List) throws IOException {

//         // public void processPermohonan(
//         // User user,
//         // Long skemaId,
//         // Long jadwalId,
//         // Long sumberId,
//         // Long pemberiId,
//         // String tujuanAsesmen,
//         // UserProfileDto userDto,
//         // Map<String, MultipartFile> fileMap,
//         // Map<String, String> apl02Data, // K/BK per elemen
//         // Map<String, String[]> apl02Bukti) // Bukti relevan per elemen
//         // throws Exception {

//         System.out.println("=== SERVICE: MEMPROSES PERMOHONAN ===");

//         // 1. UPDATE PROFILE USER (Dari Tab 2)
//         updateUserProfile(user, userDto);

//         // 2. SIMPAN PERMOHONAN SERTIFIKASI
//         PermohonanSertifikasi permohonan = new PermohonanSertifikasi();
//         permohonan.setAsesi(user);
//         permohonan.setSkema(skemaRepository.findById(skemaId).orElseThrow());
//         permohonan.setJadwal(scheduleRepository.findById(jadwalId).orElseThrow());

//         if (sumberId != null) {
//             permohonan.setSumberAnggaran(
//                     sumberAnggaranRepository.findById(sumberId).orElse(null));
//         }

//         if (pemberiId != null) {
//             permohonan.setPemberiAnggaran(
//                     pemberiAnggaranRepository.findById(pemberiId).orElse(null));
//         }

//         permohonan.setTujuanAsesmen(tujuanAsesmen);
//         permohonan.setTanggalPermohonan(LocalDateTime.now());
//         permohonan.setStatus("SUBMITTED");

//         permohonan = permohonanRepository.save(permohonan);
//         System.out.println("Permohonan disimpan dengan ID: " + permohonan.getId());

//         // 3. SIMPAN BUKTI PERSYARATAN (Tab 3)
//         saveBuktiPersyaratan(permohonan, fileMap, skemaId);

//         // 4. SIMPAN BUKTI ADMINISTRASI (Tab 4)
//         saveBuktiAdministrasi(permohonan, fileMap);

//         // 5. SIMPAN BUKTI PORTOFOLIO (Tab 5)
//         Map<String, BuktiPortofolio> portofolioMap = saveBuktiPortofolio(permohonan, fileMap);

//         // 6. SIMPAN ASESMEN MANDIRI (Tab 7) - PERBAIKAN CRITICAL
//         saveAsesmenMandiri(permohonan, apl02List, portofolioMap);

//         System.out.println("=== PROSES SELESAI ===");
//     }

//     private void updateUserProfile(User user, UserProfileDto dto) {
//         user.setNik(dto.getNik());
//         user.setFullName(dto.getFullName());
//         user.setBirthPlace(dto.getBirthPlace());
//         user.setBirthDate(dto.getBirthDate());
//         user.setGender(dto.getGender());
//         user.setAddress(dto.getAddress());
//         user.setPostalCode(dto.getPostalCode());

//         // Tambahkan +62 di depan nomor telepon
//         if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
//             user.setPhoneNumber("0" + dto.getPhoneNumber());
//         }

//         user.setEmail(dto.getEmail());
//         user.setCompanyName(dto.getCompanyName());
//         user.setPosition(dto.getPosition());
//         user.setOfficePhone(dto.getOfficePhone());
//         user.setOfficeEmail(dto.getOfficeEmail());
//         user.setOfficeFax(dto.getOfficeFax());
//         user.setOfficeAddress(dto.getOfficeAddress());
//         user.setProvinceId(dto.getProvinceId());
//         user.setCityId(dto.getCityId());
//         user.setDistrictId(dto.getDistrictId());

//         userRepository.save(user);
//         System.out.println("Profile user updated: " + user.getFullName());
//     }

//     private void saveBuktiPersyaratan(PermohonanSertifikasi permohonan,
//             Map<String, MultipartFile> fileMap,
//             Long skemaId) throws IOException {
//         System.out.println("--- Menyimpan Bukti Persyaratan ---");

//         List<PersyaratanSkema> persyaratanList = persyaratanSkemaRepository.findBySkemaId(skemaId);

//         for (int i = 0; i < persyaratanList.size(); i++) {
//             String fileKey = "syarat_" + i;

//             if (fileMap.containsKey(fileKey)) {
//                 MultipartFile file = fileMap.get(fileKey);
//                 String fileName = saveFile(file, "persyaratan");

//                 BuktiPersyaratan bukti = new BuktiPersyaratan();
//                 bukti.setPermohonan(permohonan);
//                 bukti.setPersyaratanSkema(persyaratanList.get(i));
//                 bukti.setFilePath(fileName);
//                 bukti.setStatus("PENDING"); // Akan diverifikasi admin

//                 buktiPersyaratanRepository.save(bukti);
//                 System.out.println("  Saved: " + fileKey + " -> " + fileName);
//             }
//         }
//     }

//     private void saveBuktiAdministrasi(PermohonanSertifikasi permohonan,
//             Map<String, MultipartFile> fileMap) throws IOException {
//         System.out.println("--- Menyimpan Bukti Administrasi ---");

//         // KTP
//         if (fileMap.containsKey("administrasi_1")) {
//             String fileName = saveFile(fileMap.get("administrasi_1"), "administrasi");
//             BuktiAdministrasi bukti = new BuktiAdministrasi();
//             bukti.setPermohonan(permohonan);
//             bukti.setJenisBukti("KTP");
//             bukti.setFilePath(fileName);
//             buktiAdministrasiRepository.save(bukti);
//             System.out.println("  Saved KTP: " + fileName);
//         }

//         // Pasfoto
//         if (fileMap.containsKey("administrasi_2")) {
//             String fileName = saveFile(fileMap.get("administrasi_2"), "administrasi");
//             BuktiAdministrasi bukti = new BuktiAdministrasi();
//             bukti.setPermohonan(permohonan);
//             bukti.setJenisBukti("PASFOTO");
//             bukti.setFilePath(fileName);
//             buktiAdministrasiRepository.save(bukti);
//             System.out.println("  Saved Pasfoto: " + fileName);
//         }
//     }

//     // C. Tab 5: Bukti Portofolio
//     // Key format: "portofolio_{temp_id}"
//     // Kita butuh Map untuk mapping TempID ke Entity Object agar bisa dipakai di
//     // APL-02
//     Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();

//     for(
//     String key:files.keySet())
//     {
//         if (key.startsWith("portofolio_")) {
//             String tempId = key; // e.g. "portofolio_1"
//             MultipartFile file = files.get(key);
//             String fileName = saveFile(file, user.getUsername() + "_" + tempId);

//             BuktiPortofolio bp = new BuktiPortofolio();
//             bp.setPermohonan(permohonan);
//             bp.setNamaDokumen(file.getOriginalFilename());
//             bp.setFilePath(fileName);
//             bp.setTempId(tempId); // Simpan temp ID untuk lookup nanti

//             portofolioRepository.save(bp);
//             portofolioMap.put(tempId, bp); // Masukkan ke Map
//         }
//     }

//     // D. Tab 7: Bukti Kompetensi (APL-02)
//     // Data dikirim dari JS dalam bentuk key-value param
//     // Format Key Checkbox: "kompeten_{kukId}" -> Value "K" or "BK"
//     // Format Key Bukti: "bukti_{kukId}" -> Value "portofolio_1,portofolio_2" (comma
//     // separated)

//     for (String key : apl02Data.keySet()) {
//         if (key.startsWith("kompeten_elemen_")) {
//             // Parsing Key: "kompeten_elemen_123"
//             String[] parts = key.split("_");

//             // Ambil ID dari index ke-2
//             Long elemenId = Long.parseLong(parts[2]);
//             String rekomendasi = apl02Data.get(key); // K atau BK

//             AsesmenMandiri am = new AsesmenMandiri();
//             am.setPermohonan(permohonan);

//             // Set Relasi ke UnitElemenSkema (Bukan KUK)
//             // Pastikan Anda sudah Inject UnitElemenSkemaRepository
//             am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));

//             am.setRekomendasiAsesi(rekomendasi);

//             // Link Bukti (name="bukti_elemen_123")
//             String buktiKey = "bukti_elemen_" + elemenId;
//             if (apl02Bukti.containsKey(buktiKey)) {
//                 String[] buktiIds = apl02Bukti.get(buktiKey);
//                 List<BuktiPortofolio> buktiList = new ArrayList<>();
//                 if (buktiIds != null) {
//                     for (String bId : buktiIds) {
//                         // bId contoh: "portofolio_1" -> Cari di Map
//                         if (portofolioMap.containsKey(bId)) {
//                             buktiList.add(portofolioMap.get(bId));
//                         }
//                     }
//                 }
//                 am.setBuktiRelevan(buktiList);
//             }
//             asesmenMandiriRepository.save(am);
//         }
//     }

//     // for (String key : apl02Data.keySet()) {
//     // if (key.startsWith("kompeten_elemen_")) {

//     // // Parsing ID Elemen (Aman karena split index 2)
//     // // "kompeten" "elemen" "12"
//     // String[] parts = key.split("_");
//     // Long elemenId = Long.parseLong(parts[2]);
//     // String rekomendasi = apl02Data.get(key); // "K" atau "BK"

//     // AsesmenMandiri am = new AsesmenMandiri();
//     // am.setPermohonan(permohonan);

//     // // Set Elemen (Bukan KUK lagi)
//     // am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));

//     // am.setRekomendasiAsesi(rekomendasi);

//     // // Link ke Bukti Relevan
//     // // Format key bukti: "bukti_elemen_{elemenId}"
//     // String buktiKey = "bukti_elemen_" + elemenId;

//     // if (apl02Bukti.containsKey(buktiKey)) {
//     // String[] buktiIds = apl02Bukti.get(buktiKey); // Dapat array dari select2
//     // List<BuktiPortofolio> buktiList = new ArrayList<>();

//     // if (buktiIds != null) {
//     // for (String bId : buktiIds) {
//     // // bId contoh: "portofolio_1"
//     // if (portofolioMap.containsKey(bId)) {
//     // buktiList.add(portofolioMap.get(bId));
//     // }
//     // }
//     // }
//     // am.setBuktiRelevan(buktiList);
//     // }

//     // asesmenMandiriRepository.save(am);
//     // }
//     // }

//     // private Map<String, BuktiPortofolio>
//     // saveBuktiPortofolio(PermohonanSertifikasi permohonan,
//     // Map<String, MultipartFile> fileMap) throws IOException {
//     // System.out.println("--- Menyimpan Bukti Portofolio ---");

//     // Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();
//     // String[] portofolioKeys = { "portofolio_1", "portofolio_2", "portofolio_3" };
//     // String[] portofolioNames = { "Laporan Projek", "Sertifikat Pelatihan", "Surat
//     // Tugas" };

//     // for (int i = 0; i < portofolioKeys.length; i++) {
//     // String key = portofolioKeys[i];

//     // if (fileMap.containsKey(key)) {
//     // String fileName = saveFile(fileMap.get(key), "portofolio");

//     // BuktiPortofolio bukti = new BuktiPortofolio();
//     // bukti.setPermohonan(permohonan);
//     // bukti.setNamaDokumen(portofolioNames[i]);
//     // bukti.setFilePath(fileName);

//     // bukti = buktiPortofolioRepository.save(bukti);

//     // // Simpan mapping ID frontend -> Object DB
//     // portofolioMap.put(key, bukti);

//     // System.out.println(" Saved: " + key + " -> " + fileName + " (DB ID: " +
//     // bukti.getId() + ")");
//     // }
//     // }

//     // return portofolioMap;
//     // }

//     // private void saveAsesmenMandiri(PermohonanSertifikasi permohonan,
//     // List<Map<String, Object>> apl02List,
//     // Map<String, BuktiPortofolio> portofolioMap) {
//     // System.out.println("--- Menyimpan Asesmen Mandiri (APL-02) ---");

//     // // Loop setiap rekomendasi K/BK
//     // for (Map<String, Object> entry : apl02List) {
//     // String key = entry.getKey(); // Format: "kompeten_elemen_{ID_ELEMEN}"
//     // String rekomendasi = (String) entry.getValue(); // "K" atau "BK"

//     // // Extract ID Elemen dari key
//     // Long elemenId = extractElemenId(key);

//     // if (elemenId == null) {
//     // System.out.println(" SKIP: Tidak dapat extract ID dari key: " + key);
//     // continue;
//     // }

//     // // Cari Unit Elemen di Database
//     // UnitElemenSkema elemen =
//     // unitElemenRepository.findById(elemenId).orElse(null);

//     // if (elemen == null) {
//     // System.out.println(" SKIP: Elemen tidak ditemukan di DB: " + elemenId);
//     // continue;
//     // }

//     // // Buat record Asesmen Mandiri
//     // AsesmenMandiri asesmen = new AsesmenMandiri();
//     // asesmen.setPermohonan(permohonan);
//     // asesmen.setUnitElemen(elemen);
//     // asesmen.setRekomendasiAsesi(rekomendasi);

//     // // Ambil Bukti Relevan (jika ada)
//     // String buktiKey = key.replace("kompeten_", "bukti_"); // "bukti_elemen_{ID}"

//     // if (apl02Bukti.containsKey(buktiKey)) {
//     // String[] buktiIds = apl02Bukti.get(buktiKey);
//     // List<BuktiPortofolio> buktiList = new ArrayList<>();

//     // for (String buktiId : buktiIds) {
//     // // buktiId format: "portofolio_1", "portofolio_2", dst
//     // BuktiPortofolio bukti = portofolioMap.get(buktiId);

//     // if (bukti != null) {
//     // buktiList.add(bukti);
//     // } else {
//     // System.out.println(" WARNING: Bukti tidak ditemukan: " + buktiId);
//     // }
//     // }

//     // asesmen.setBuktiRelevan(buktiList);
//     // }

//     // asesmenMandiriRepository.save(asesmen);

//     // System.out.println(" Saved: Elemen ID " + elemenId + " | Rekomendasi: " +
//     // rekomendasi +
//     // " | Bukti: " + (asesmen.getBuktiRelevan() != null ?
//     // asesmen.getBuktiRelevan().size() : 0));
//     // }
//     // }

//     // Helper: Extract Elemen ID dari key
//     private Long extractElemenId(String key) {
//         // Format key: "kompeten_elemen_{ID}"
//         try {
//             String[] parts = key.split("_");
//             return Long.parseLong(parts[parts.length - 1]);
//         } catch (Exception e) {
//             System.out.println("ERROR extractElemenId: " + e.getMessage());
//             return null;
//         }
//     }

//     private String saveFile(MultipartFile file, String prefix) throws IOException {
//         String originalName = file.getOriginalFilename();
//         String ext = originalName.substring(originalName.lastIndexOf("."));
//         String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8)
//                 + ext;

//         Path path = Paths.get(UPLOAD_DIR + fileName);
//         Files.createDirectories(path.getParent());
//         Files.write(path, file.getBytes());

//         return fileName;
//     }

//     // Helper: Save file to disk
//     // private String saveFile(MultipartFile file, String folder) throws IOException
//     // {
//     // String uploadDir = "uploads/" + folder + "/";
//     // File dir = new File(uploadDir);
//     // if (!dir.exists()) {
//     // dir.mkdirs();
//     // }

//     // String fileName = System.currentTimeMillis() + "_" +
//     // file.getOriginalFilename();
//     // String filePath = uploadDir + fileName;

//     // file.transferTo(new File(filePath));

//     // return fileName;
//     // }
// }

// @Transactional
// public void processPermohonan(
// User user,
// Long skemaId,
// Long jadwalId,
// Long sumberAnggaranId, // Ubah parameter jadi Long ID
// Long pemberiAnggaranId, // Ubah parameter jadi Long ID
// String tujuanAsesmen,
// UserProfileDto userUpdateData,
// Map<String, MultipartFile> files, // Semua file (Syarat, Admin, Portofolio)
// Map<String, String> apl02Data, // Data K/BK
// Map<String, String[]> apl02Bukti // Data Bukti (Array)
// ) throws IOException {

// // 1. UPDATE DATA PEMOHON (USER PROFILE)
// updateUserData(user, userUpdateData);

// // 2. SIMPAN HEADER PERMOHONAN
// PermohonanSertifikasi permohonan = new PermohonanSertifikasi();
// permohonan.setAsesi(user);
// permohonan.setSkema(skemaRepository.findById(skemaId).orElseThrow());
// permohonan.setJadwal(scheduleRepository.findById(jadwalId).orElseThrow());
// if (sumberAnggaranId != null)
// permohonan.setSumberAnggaran(sumberAnggaranRepository.findById(sumberAnggaranId).orElse(null));
// if (pemberiAnggaranId != null)
// permohonan.setPemberiAnggaran(pemberiAnggaranRepository.findById(pemberiAnggaranId).orElse(null));
// permohonan.setTujuanAsesmen(tujuanAsesmen);
// permohonan = permohonanRepository.save(permohonan);

// // 3. PROSES FILE & DATA TAB

// // A. Tab 3: Persyaratan Dasar
// // Key format dari JS: "syarat_{id_persyaratan_db}"
// for (String key : files.keySet()) {
// if (key.startsWith("syarat_")) {
// Long syaratId = Long.parseLong(key.split("_")[1]);
// String fileName = saveFile(files.get(key), user.getUsername() + "_syarat_" +
// syaratId);

// BuktiPersyaratan pp = new BuktiPersyaratan();
// pp.setPermohonan(permohonan);
// pp.setPersyaratanSkema(persyaratanSkemaRepository.findById(syaratId).orElse(null));
// pp.setFilePath(fileName);
// pp.setStatus("PENDING");
// persyaratanRepository.save(pp);
// }
// }

// // B. Tab 4: Bukti Administrasi
// // Key format: "administrasi_{tipe}" (1=KTP, 2=Foto)
// for (String key : files.keySet()) {
// if (key.startsWith("administrasi_")) {
// String tipe = key.split("_")[1]; // "1" or "2"
// String namaDok = tipe.equals("1") ? "KTP" : "Pas Foto";
// String fileName = saveFile(files.get(key), user.getUsername() + "_admin_" +
// tipe);

// BuktiAdministrasi ba = new BuktiAdministrasi();
// ba.setPermohonan(permohonan);
// ba.setJenisBukti(namaDok);
// ba.setFilePath(fileName);
// buktiAdminRepository.save(ba);
// }
// }

// // C. Tab 5: Bukti Portofolio
// // Key format: "portofolio_{temp_id}"
// // Kita butuh Map untuk mapping TempID ke Entity Object agar bisa dipakai di
// // APL-02
// Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();

// for (String key : files.keySet()) {
// if (key.startsWith("portofolio_")) {
// String tempId = key; // e.g. "portofolio_1"
// MultipartFile file = files.get(key);
// String fileName = saveFile(file, user.getUsername() + "_" + tempId);

// BuktiPortofolio bp = new BuktiPortofolio();
// bp.setPermohonan(permohonan);
// bp.setNamaDokumen(file.getOriginalFilename());
// bp.setFilePath(fileName);
// bp.setTempId(tempId); // Simpan temp ID untuk lookup nanti

// portofolioRepository.save(bp);
// portofolioMap.put(tempId, bp); // Masukkan ke Map
// }
// }

// // D. Tab 7: Bukti Kompetensi (APL-02)
// // Data dikirim dari JS dalam bentuk key-value param
// // Format Key Checkbox: "kompeten_{kukId}" -> Value "K" or "BK"
// // Format Key Bukti: "bukti_{kukId}" -> Value "portofolio_1,portofolio_2"
// (comma
// // separated)

// // for (String key : apl02Data.keySet()) {
// // if (key.startsWith("kompeten_")) {
// // Long kukId = Long.parseLong(key.split("_")[1]);
// // String rekomendasi = apl02Data.get(key); // "K" or "BK"

// // AsesmenMandiri am = new AsesmenMandiri();
// // am.setPermohonan(permohonan);
// // am.setKuk(kukSkemaRepository.findById(kukId).orElse(null));
// // am.setRekomendasiAsesi(rekomendasi);

// // // Link ke Bukti Relevan
// // String buktiKey = "bukti_" + kukId;
// // if (apl02Data.containsKey(buktiKey)) {
// // String[] buktiIds = apl02Data.get(buktiKey).split(",");
// // List<BuktiPortofolio> buktiList = new ArrayList<>();

// // for (String bId : buktiIds) {
// // // bId misal "portofolio_1". Cari di Map yang sudah disimpan tadi
// // if (portofolioMap.containsKey(bId)) {
// // buktiList.add(portofolioMap.get(bId));
// // }
// // }
// // am.setBuktiRelevan(buktiList);
// // }

// // asesmenMandiriRepository.save(am);
// // }
// // }

// // for (String key : apl02Data.keySet()) {
// // if (key.startsWith("kompeten_elemen_")) {

// // // Parsing ID Elemen (Aman karena split index 2)
// // // "kompeten" "elemen" "12"
// // String[] parts = key.split("_");
// // Long elemenId = Long.parseLong(parts[2]);
// // String rekomendasi = apl02Data.get(key); // "K" atau "BK"

// // AsesmenMandiri am = new AsesmenMandiri();
// // am.setPermohonan(permohonan);

// // // Set Elemen (Bukan KUK lagi)
// //
// am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));

// // am.setRekomendasiAsesi(rekomendasi);

// // // Link ke Bukti Relevan
// // // Format key bukti: "bukti_elemen_{elemenId}"
// // String buktiKey = "bukti_elemen_" + elemenId;

// // if (apl02Bukti.containsKey(buktiKey)) {
// // String[] buktiIds = apl02Bukti.get(buktiKey); // Dapat array dari select2
// // List<BuktiPortofolio> buktiList = new ArrayList<>();

// // if (buktiIds != null) {
// // for (String bId : buktiIds) {
// // // bId contoh: "portofolio_1"
// // if (portofolioMap.containsKey(bId)) {
// // buktiList.add(portofolioMap.get(bId));
// // }
// // }
// // }
// // am.setBuktiRelevan(buktiList);
// // }

// // asesmenMandiriRepository.save(am);
// // }
// // }
// // }

// for(

// String key:apl02Data.keySet())
// {
// if (key.startsWith("kompeten_elemen_")) {

// // Format Key: "kompeten_elemen_{ID_ELEMEN}"
// // Split: [0]="kompeten", [1]="elemen", [2]="{ID}"
// String[] parts = key.split("_");
// Long elemenId = Long.parseLong(parts[2]);
// String rekomendasi = apl02Data.get(key); // Value: "K" atau "BK"

// AsesmenMandiri am = new AsesmenMandiri();
// am.setPermohonan(permohonan);

// // Set Relasi ke Elemen
// am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));
// am.setRekomendasiAsesi(rekomendasi);

// // --- LINK KE BUKTI RELEVAN ---
// // Cari key bukti yang sesuai dengan ID Elemen ini
// String buktiKey = "bukti_elemen_" + elemenId;

// if (apl02Bukti.containsKey(buktiKey)) {
// // Ambil array ID sementara dari Select2 (misal: ["portofolio_1",
// // "portofolio_2"])
// String[] buktiTempIds = apl02Bukti.get(buktiKey);

// List<BuktiPortofolio> buktiList = new ArrayList<>();
// if (buktiTempIds != null) {
// for (String bId : buktiTempIds) {
// // Cari object asli di Map yang sudah kita simpan di langkah no.3
// if (portofolioMap.containsKey(bId)) {
// buktiList.add(portofolioMap.get(bId));
// }
// }
// }
// am.setBuktiRelevan(buktiList);
// }

// asesmenMandiriRepository.save(am);
// }
// }
// }

// private void updateUserData(User user, UserProfileDto dto) {
// // Update field yang diperbolehkan
// user.setFullName(dto.getFullName());
// user.setBirthPlace(dto.getBirthPlace());
// user.setBirthDate(dto.getBirthDate());
// user.setGender(dto.getGender());
// user.setEmail(dto.getEmail());
// // user.setNoTelp(dto.getPhoneNumber());
// user.setProvinceId(dto.getProvinceId());
// user.setCityId(dto.getCityId());
// user.setDistrictId(dto.getDistrictId());
// user.setAddress(dto.getAddress());
// user.setPostalCode(dto.getPostalCode());

// // 2. NO TELP: Hapus '0' di depan agar di form jadi (811...)
// if (dto.getPhoneNumber() != null) {
// String rawPhone = dto.getPhoneNumber().replaceAll("[^0-9]", "");
// if (rawPhone.startsWith("0"))
// rawPhone = rawPhone.substring(1);
// user.setPhoneNumber("0" + rawPhone);
// }

// // 1. NIK: Bersihkan titik lagi sebelum simpan update
// if (dto.getNik() != null) {
// user.setNik(dto.getNik().replaceAll("[^0-9]", ""));
// }
// // Mapping Relasi ID (3NF)
// // Mapping Relasi ID (3NF)
// if (dto.getEducationId() != null)
// user.setEducationId(educationRepository.findById(dto.getEducationId()).orElse(null));
// if (dto.getJobTypeId() != null)
// user.setJobTypeId(jobTypeRepository.findById(dto.getJobTypeId()).orElse(null));

// user.setCompanyName(dto.getCompanyName());
// user.setPosition(dto.getPosition());
// user.setOfficePhone(dto.getOfficePhone());
// user.setOfficeEmail(dto.getOfficeEmail());
// user.setOfficeFax(dto.getOfficeFax());
// user.setOfficeAddress(dto.getOfficeAddress());

// userRepository.save(user);
// }

// private String saveFile(MultipartFile file, String prefix) throws IOException
// {
// String originalName = file.getOriginalFilename();
// String ext = originalName.substring(originalName.lastIndexOf("."));
// String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8)
// + ext;

// Path path = Paths.get(UPLOAD_DIR + fileName);
// Files.createDirectories(path.getParent());
// Files.write(path, file.getBytes());

// return fileName;
// }

package com.lsptddi.silsp.service;

import com.lsptddi.silsp.dto.SertifikasiRequestDto;
import com.lsptddi.silsp.dto.UserProfileDto;
import com.lsptddi.silsp.model.*;
import com.lsptddi.silsp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class PermohonanService {

    @Autowired
    private PermohonanSertifikasiRepository permohonanRepository;
    @Autowired
    private BuktiPersyaratanRepository persyaratanRepository;
    @Autowired
    private BuktiAdministrasiRepository buktiAdminRepository;
    @Autowired
    private BuktiPortofolioRepository portofolioRepository;
    @Autowired
    private AsesmenMandiriRepository asesmenMandiriRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkemaRepository skemaRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PersyaratanSkemaRepository persyaratanSkemaRepository;

    @Autowired
    private UnitElemenSkemaRepository unitElemenSkemaRepository;
    @Autowired
    private KukSkemaRepository kukSkemaRepository;
    @Autowired
    private TypePekerjaanRepository jobTypeRepository;
    @Autowired
    private TypeEducationRepository educationRepository;
    @Autowired
    private TypeSumberAnggaranRepository sumberAnggaranRepository;
    @Autowired
    private TypePemberiAnggaranRepository pemberiAnggaranRepository;

    private final String UPLOAD_DIR = "uploads/permohonan/";

    @Transactional
    public void processPermohonanJson(
            User user,
            SertifikasiRequestDto dto,
            Map<String, MultipartFile> files) throws IOException {

        // 1. UPDATE DATA USER
        if (dto.getDataPemohon() != null) {
            updateUserData(user, dto.getDataPemohon());
        }

        // 2. SIMPAN HEADER
        PermohonanSertifikasi permohonan = new PermohonanSertifikasi();
        permohonan.setAsesi(user);
        permohonan.setSkema(skemaRepository.findById(dto.getSkemaId()).orElseThrow());
        permohonan.setJadwal(scheduleRepository.findById(dto.getJadwalId()).orElseThrow());
        if (dto.getSumberAnggaranId() != null)
            permohonan.setSumberAnggaran(sumberAnggaranRepository.findById(dto.getSumberAnggaranId()).orElse(null));
        if (dto.getPemberiAnggaranId() != null)
            permohonan.setPemberiAnggaran(pemberiAnggaranRepository.findById(dto.getPemberiAnggaranId()).orElse(null));
        // permohonan.setSumberAnggaran(dto.getSumberAnggaran());
        // permohonan.setPemberiAnggaran(dto.getPemberiAnggaran());
        permohonan.setTujuanAsesmen(dto.getTujuanAsesmen());
        permohonan = permohonanRepository.save(permohonan);

        // 3. SIMPAN FILES (Persyaratan, Admin, Portofolio)
        // Kita simpan mapping TempID ("portofolio_1") ke Entity Database agar bisa
        // dipakai di APL-02
        // Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();

        // for (String key : files.keySet()) {
        // MultipartFile file = files.get(key);
        // String fileName = saveFile(file, user.getUsername() + "_" + key);

        // if (key.startsWith("syarat_")) {
        // Long syaratId = Long.parseLong(key.split("_")[1]);
        // BuktiPersyaratan pp = new BuktiPersyaratan();
        // pp.setPermohonan(permohonan);
        // pp.setPersyaratanSkema(persyaratanSkemaRepository.findById(syaratId).orElse(null));
        // pp.setFilePath(fileName);
        // pp.setStatus("PENDING");
        // persyaratanRepository.save(pp);
        // }
        // else if (key.startsWith("administrasi_")) {
        // String jenis = key.contains("1") ? "KTP" : "FOTO";
        // BuktiAdministrasi ba = new BuktiAdministrasi();
        // ba.setPermohonan(permohonan);
        // ba.setJenisBukti(jenis);
        // ba.setFilePath(fileName);
        // buktiAdminRepository.save(ba);
        // } else if (key.startsWith("portofolio_")) {
        // // Simpan Portofolio
        // BuktiPortofolio bp = new BuktiPortofolio();
        // bp.setPermohonan(permohonan);
        // bp.setNamaDokumen(file.getOriginalFilename());
        // bp.setFilePath(fileName);
        // bp.setTempId(key); // simpan key sementara
        // bp = portofolioRepository.save(bp);

        // // Masukkan ke Map untuk lookup APL-02
        // portofolioMap.put(key, bp);
        // }
        // }

        Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();

        for (String key : files.keySet()) {
            MultipartFile file = files.get(key);

            // Simpan fisik file ke folder
            String fileName = saveFile(file, user.getUsername() + "_" + key);

            // A. Handle Persyaratan Dasar
            // Format key dari Frontend: "syarat_{ID_DATABASE}" (Contoh: syarat_15)
            if (key.startsWith("syarat_")) {
                try {
                    // Ambil ID dari string key
                    Long syaratId = Long.parseLong(key.split("_")[1]);

                    BuktiPersyaratan bp = new BuktiPersyaratan();
                    bp.setPermohonan(permohonan);

                    // PENTING: Cari Data Master Persyaratan berdasarkan ID
                    PersyaratanSkema masterSyarat = persyaratanSkemaRepository.findById(syaratId).orElse(null);

                    if (masterSyarat != null) {
                        bp.setPersyaratanSkema(masterSyarat); // Relasikan ke Master
                        bp.setFilePath(fileName);
                        bp.setStatus("PENDING");
                        persyaratanRepository.save(bp); // Simpan
                    } else {
                        System.out.println("Warning: Persyaratan ID " + syaratId + " tidak ditemukan di database.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing ID Persyaratan dari key: " + key);
                }
            }

            // B. Handle Bukti Administrasi
            // Format key: "administrasi_1" (KTP) atau "administrasi_2" (Foto)
            else if (key.startsWith("administrasi_")) {
                String jenis = key.contains("1") ? "KTP" : "PASFOTO";

                BuktiAdministrasi ba = new BuktiAdministrasi();
                ba.setPermohonan(permohonan);
                ba.setJenisBukti(jenis);
                ba.setFilePath(fileName);
                buktiAdminRepository.save(ba);
            }

            // C. Handle Portofolio
            // Format key: "portofolio_{TEMP_ID}" (Contoh: portofolio_1, portofolio_2)
            else if (key.startsWith("portofolio_")) {
                BuktiPortofolio bp = new BuktiPortofolio();
                bp.setPermohonan(permohonan);
                bp.setNamaDokumen(file.getOriginalFilename());
                bp.setFilePath(fileName);
                bp.setTempId(key); // Simpan key sementara (opsional, tapi berguna)

                // Simpan ke DB
                bp = portofolioRepository.save(bp);

                // PENTING: Masukkan ke Map agar bisa dipakai di loop APL-02 (Tab 7) nanti
                portofolioMap.put(key, bp);
            }
        }

        // 4. SIMPAN APL-02 (Looping dari List Object, bukan String parsing)
        if (dto.getAsesmenMandiri() != null) {
            for (SertifikasiRequestDto.Apl02ItemDto item : dto.getAsesmenMandiri()) {
                AsesmenMandiri am = new AsesmenMandiri();
                am.setPermohonan(permohonan);
                am.setUnitElemen(unitElemenSkemaRepository.findById(item.getElemenId()).orElse(null));
                am.setRekomendasiAsesi(item.getStatus());

                // Hubungkan Bukti
                List<BuktiPortofolio> relasiBukti = new ArrayList<>();
                if (item.getBuktiIds() != null) {
                    for (String tempId : item.getBuktiIds()) {
                        if (portofolioMap.containsKey(tempId)) {
                            relasiBukti.add(portofolioMap.get(tempId));
                        }
                    }
                }
                am.setBuktiRelevan(relasiBukti);
                asesmenMandiriRepository.save(am);
            }
        }
    }

    // @Transactional
    // public void processPermohonan(
    // User user,
    // Long skemaId,
    // Long jadwalId,
    // Long sumberAnggaranId, // Ubah parameter jadi Long ID
    // Long pemberiAnggaranId, // Ubah parameter jadi Long ID
    // String tujuanAsesmen,
    // UserProfileDto userUpdateData,
    // Map<String, MultipartFile> files,
    // // Param Baru: List Data dari JSON
    // List<Map<String, Object>> apl02List) throws IOException {

    // // 1. UPDATE DATA PEMOHON (USER PROFILE)
    // updateUserData(user, userUpdateData);

    // // 2. SIMPAN HEADER PERMOHONAN
    // PermohonanSertifikasi permohonan = new PermohonanSertifikasi();
    // permohonan.setAsesi(user);
    // permohonan.setSkema(skemaRepository.findById(skemaId).orElseThrow());
    // permohonan.setJadwal(scheduleRepository.findById(jadwalId).orElseThrow());
    // if (sumberAnggaranId != null)
    // permohonan.setSumberAnggaran(sumberAnggaranRepository.findById(sumberAnggaranId).orElse(null));
    // if (pemberiAnggaranId != null)
    // permohonan.setPemberiAnggaran(pemberiAnggaranRepository.findById(pemberiAnggaranId).orElse(null));
    // permohonan.setTujuanAsesmen(tujuanAsesmen);
    // permohonan = permohonanRepository.save(permohonan);

    // // 3. PROSES FILE & DATA TAB

    // // A. Tab 3: Persyaratan Dasar
    // // Key format dari JS: "syarat_{id_persyaratan_db}"
    // for (String key : files.keySet()) {
    // if (key.startsWith("syarat_")) {
    // Long syaratId = Long.parseLong(key.split("_")[1]);
    // String fileName = saveFile(files.get(key), user.getUsername() + "_syarat_" +
    // syaratId);

    // BuktiPersyaratan pp = new BuktiPersyaratan();
    // pp.setPermohonan(permohonan);
    // pp.setPersyaratanSkema(persyaratanSkemaRepository.findById(syaratId).orElse(null));
    // pp.setFilePath(fileName);
    // pp.setStatus("PENDING");
    // persyaratanRepository.save(pp);
    // }
    // }

    // // B. Tab 4: Bukti Administrasi
    // // Key format: "administrasi_{tipe}" (1=KTP, 2=Foto)
    // for (String key : files.keySet()) {
    // if (key.startsWith("administrasi_")) {
    // String tipe = key.split("_")[1]; // "1" or "2"
    // String namaDok = tipe.equals("1") ? "KTP" : "Pas Foto";
    // String fileName = saveFile(files.get(key), user.getUsername() + "_admin_" +
    // tipe);

    // BuktiAdministrasi ba = new BuktiAdministrasi();
    // ba.setPermohonan(permohonan);
    // ba.setJenisBukti(namaDok);
    // ba.setFilePath(fileName);
    // buktiAdminRepository.save(ba);
    // }
    // }

    // // C. Tab 5: Bukti Portofolio
    // // Key format: "portofolio_{temp_id}"
    // // Kita butuh Map untuk mapping TempID ke Entity Object agar bisa dipakai di
    // // APL-02
    // Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();

    // for (String key : files.keySet()) {
    // if (key.startsWith("portofolio_")) {
    // String tempId = key; // e.g. "portofolio_1"
    // MultipartFile file = files.get(key);
    // String fileName = saveFile(file, user.getUsername() + "_" + tempId);

    // BuktiPortofolio bp = new BuktiPortofolio();
    // bp.setPermohonan(permohonan);
    // bp.setNamaDokumen(file.getOriginalFilename());
    // bp.setFilePath(fileName);
    // bp.setTempId(tempId); // Simpan temp ID untuk lookup nanti

    // portofolioRepository.save(bp);
    // portofolioMap.put(tempId, bp); // Masukkan ke Map
    // }
    // }

    // // D. Tab 7: Bukti Kompetensi (APL-02)
    // // Data dikirim dari JS dalam bentuk key-value param
    // // Format Key Checkbox: "kompeten_{kukId}" -> Value "K" or "BK"
    // // Format Key Bukti: "bukti_{kukId}" -> Value "portofolio_1,portofolio_2"
    // (comma
    // // separated)

    // // for (String key : apl02Data.keySet()) {
    // // if (key.startsWith("kompeten_")) {
    // // Long kukId = Long.parseLong(key.split("_")[1]);
    // // String rekomendasi = apl02Data.get(key); // "K" or "BK"

    // // AsesmenMandiri am = new AsesmenMandiri();
    // // am.setPermohonan(permohonan);
    // // am.setKuk(kukSkemaRepository.findById(kukId).orElse(null));
    // // am.setRekomendasiAsesi(rekomendasi);

    // // // Link ke Bukti Relevan
    // // String buktiKey = "bukti_" + kukId;
    // // if (apl02Data.containsKey(buktiKey)) {
    // // String[] buktiIds = apl02Data.get(buktiKey).split(",");
    // // List<BuktiPortofolio> buktiList = new ArrayList<>();

    // // for (String bId : buktiIds) {
    // // // bId misal "portofolio_1". Cari di Map yang sudah disimpan tadi
    // // if (portofolioMap.containsKey(bId)) {
    // // buktiList.add(portofolioMap.get(bId));
    // // }
    // // }
    // // am.setBuktiRelevan(buktiList);
    // // }

    // // asesmenMandiriRepository.save(am);
    // // }
    // // }

    // // for (String key : apl02Data.keySet()) {
    // // if (key.startsWith("kompeten_elemen_")) {

    // // // Parsing ID Elemen (Aman karena split index 2)
    // // // "kompeten" "elemen" "12"
    // // String[] parts = key.split("_");
    // // Long elemenId = Long.parseLong(parts[2]);
    // // String rekomendasi = apl02Data.get(key); // "K" atau "BK"

    // // AsesmenMandiri am = new AsesmenMandiri();
    // // am.setPermohonan(permohonan);

    // // // Set Elemen (Bukan KUK lagi)
    // //
    // am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));

    // // am.setRekomendasiAsesi(rekomendasi);

    // // // Link ke Bukti Relevan
    // // // Format key bukti: "bukti_elemen_{elemenId}"
    // // String buktiKey = "bukti_elemen_" + elemenId;

    // // if (apl02Bukti.containsKey(buktiKey)) {
    // // String[] buktiIds = apl02Bukti.get(buktiKey); // Dapat array dari select2
    // // List<BuktiPortofolio> buktiList = new ArrayList<>();

    // // if (buktiIds != null) {
    // // for (String bId : buktiIds) {
    // // // bId contoh: "portofolio_1"
    // // if (portofolioMap.containsKey(bId)) {
    // // buktiList.add(portofolioMap.get(bId));
    // // }
    // // }
    // // }
    // // am.setBuktiRelevan(buktiList);
    // // }

    // // asesmenMandiriRepository.save(am);
    // // }
    // // }

    // if (apl02List != null) {
    // for (Map<String, Object> item : apl02List) {
    // // Item structure: {elemenId: 1, status: "K", buktiIds: ["port_1", "port_2"]}

    // Long elemenId = Long.valueOf(item.get("elemenId").toString());
    // String rekomendasi = (String) item.get("status");
    // List<String> buktiIds = (List<String>) item.get("buktiIds");

    // AsesmenMandiri am = new AsesmenMandiri();
    // am.setPermohonan(permohonan);

    // // Set Relasi Elemen (Penting!)
    // am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));
    // am.setRekomendasiAsesi(rekomendasi);

    // // Mapping Bukti Relevan
    // List<BuktiPortofolio> relasiBukti = new ArrayList<>();
    // if (buktiIds != null) {
    // for (String bId : buktiIds) {
    // // Cari ID asli di Map berdasarkan ID sementara dari JS
    // if (portofolioMap.containsKey(bId)) {
    // relasiBukti.add(portofolioMap.get(bId));
    // }
    // }
    // }
    // am.setBuktiRelevan(relasiBukti);

    // asesmenMandiriRepository.save(am);
    // }
    // }
    // }

    private void updateUserData(User user, UserProfileDto dto) {
        // Update field yang diperbolehkan
        user.setFullName(dto.getFullName());
        user.setBirthPlace(dto.getBirthPlace());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setEmail(dto.getEmail());
        // user.setNoTelp(dto.getPhoneNumber());
        user.setProvinceId(dto.getProvinceId());
        user.setCityId(dto.getCityId());
        user.setDistrictId(dto.getDistrictId());
        user.setAddress(dto.getAddress());
        user.setPostalCode(dto.getPostalCode());

        // 2. NO TELP: Hapus '0' di depan agar di form jadi (811...)
        if (dto.getPhoneNumber() != null) {
            String rawPhone = dto.getPhoneNumber().replaceAll("[^0-9]", "");
            if (rawPhone.startsWith("0"))
                rawPhone = rawPhone.substring(1);
            user.setPhoneNumber("0" + rawPhone);
        }

        // 1. NIK: Bersihkan titik lagi sebelum simpan update
        if (dto.getNik() != null) {
            user.setNik(dto.getNik().replaceAll("[^0-9]", ""));
        }
        // Mapping Relasi ID (3NF)
        // Mapping Relasi ID (3NF)
        if (dto.getEducationId() != null)
            user.setEducationId(educationRepository.findById(dto.getEducationId()).orElse(null));
        if (dto.getJobTypeId() != null)
            user.setJobTypeId(jobTypeRepository.findById(dto.getJobTypeId()).orElse(null));

        user.setCompanyName(dto.getCompanyName());
        user.setPosition(dto.getPosition());
        user.setOfficePhone(dto.getOfficePhone());
        user.setOfficeEmail(dto.getOfficeEmail());
        user.setOfficeFax(dto.getOfficeFax());
        user.setOfficeAddress(dto.getOfficeAddress());

        userRepository.save(user);
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return fileName;
    }
}