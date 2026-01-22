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

package com.lsptddi.silsp.service;

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
    public void processPermohonan(
            User user,
            Long skemaId,
            Long jadwalId,
            Long sumberAnggaranId, // Ubah parameter jadi Long ID
            Long pemberiAnggaranId, // Ubah parameter jadi Long ID
            String tujuanAsesmen,
            UserProfileDto userUpdateData,
            Map<String, MultipartFile> files, // Semua file (Syarat, Admin, Portofolio)
            Map<String, String> apl02Data, // Data K/BK
            Map<String, String[]> apl02Bukti // Data Bukti (Array)
    ) throws IOException {

        // 1. UPDATE DATA PEMOHON (USER PROFILE)
        updateUserData(user, userUpdateData);

        // 2. SIMPAN HEADER PERMOHONAN
        PermohonanSertifikasi permohonan = new PermohonanSertifikasi();
        permohonan.setAsesi(user);
        permohonan.setSkema(skemaRepository.findById(skemaId).orElseThrow());
        permohonan.setJadwal(scheduleRepository.findById(jadwalId).orElseThrow());
        if (sumberAnggaranId != null)
            permohonan.setSumberAnggaran(sumberAnggaranRepository.findById(sumberAnggaranId).orElse(null));
        if (pemberiAnggaranId != null)
            permohonan.setPemberiAnggaran(pemberiAnggaranRepository.findById(pemberiAnggaranId).orElse(null));
        permohonan.setTujuanAsesmen(tujuanAsesmen);
        permohonan = permohonanRepository.save(permohonan);

        // 3. PROSES FILE & DATA TAB

        // A. Tab 3: Persyaratan Dasar
        // Key format dari JS: "syarat_{id_persyaratan_db}"
        for (String key : files.keySet()) {
            if (key.startsWith("syarat_")) {
                Long syaratId = Long.parseLong(key.split("_")[1]);
                String fileName = saveFile(files.get(key), user.getUsername() + "_syarat_" + syaratId);

                BuktiPersyaratan pp = new BuktiPersyaratan();
                pp.setPermohonan(permohonan);
                pp.setPersyaratanSkema(persyaratanSkemaRepository.findById(syaratId).orElse(null));
                pp.setFilePath(fileName);
                pp.setStatus("PENDING");
                persyaratanRepository.save(pp);
            }
        }

        // B. Tab 4: Bukti Administrasi
        // Key format: "administrasi_{tipe}" (1=KTP, 2=Foto)
        for (String key : files.keySet()) {
            if (key.startsWith("administrasi_")) {
                String tipe = key.split("_")[1]; // "1" or "2"
                String namaDok = tipe.equals("1") ? "KTP" : "Pas Foto";
                String fileName = saveFile(files.get(key), user.getUsername() + "_admin_" + tipe);

                BuktiAdministrasi ba = new BuktiAdministrasi();
                ba.setPermohonan(permohonan);
                ba.setJenisBukti(namaDok);
                ba.setFilePath(fileName);
                buktiAdminRepository.save(ba);
            }
        }

        // C. Tab 5: Bukti Portofolio
        // Key format: "portofolio_{temp_id}"
        // Kita butuh Map untuk mapping TempID ke Entity Object agar bisa dipakai di
        // APL-02
        Map<String, BuktiPortofolio> portofolioMap = new HashMap<>();

        for (String key : files.keySet()) {
            if (key.startsWith("portofolio_")) {
                String tempId = key; // e.g. "portofolio_1"
                MultipartFile file = files.get(key);
                String fileName = saveFile(file, user.getUsername() + "_" + tempId);

                BuktiPortofolio bp = new BuktiPortofolio();
                bp.setPermohonan(permohonan);
                bp.setNamaDokumen(file.getOriginalFilename());
                bp.setFilePath(fileName);
                bp.setTempId(tempId); // Simpan temp ID untuk lookup nanti

                portofolioRepository.save(bp);
                portofolioMap.put(tempId, bp); // Masukkan ke Map
            }
        }

        // D. Tab 7: Bukti Kompetensi (APL-02)
        // Data dikirim dari JS dalam bentuk key-value param
        // Format Key Checkbox: "kompeten_{kukId}" -> Value "K" or "BK"
        // Format Key Bukti: "bukti_{kukId}" -> Value "portofolio_1,portofolio_2" (comma
        // separated)

        // for (String key : apl02Data.keySet()) {
        // if (key.startsWith("kompeten_")) {
        // Long kukId = Long.parseLong(key.split("_")[1]);
        // String rekomendasi = apl02Data.get(key); // "K" or "BK"

        // AsesmenMandiri am = new AsesmenMandiri();
        // am.setPermohonan(permohonan);
        // am.setKuk(kukSkemaRepository.findById(kukId).orElse(null));
        // am.setRekomendasiAsesi(rekomendasi);

        // // Link ke Bukti Relevan
        // String buktiKey = "bukti_" + kukId;
        // if (apl02Data.containsKey(buktiKey)) {
        // String[] buktiIds = apl02Data.get(buktiKey).split(",");
        // List<BuktiPortofolio> buktiList = new ArrayList<>();

        // for (String bId : buktiIds) {
        // // bId misal "portofolio_1". Cari di Map yang sudah disimpan tadi
        // if (portofolioMap.containsKey(bId)) {
        // buktiList.add(portofolioMap.get(bId));
        // }
        // }
        // am.setBuktiRelevan(buktiList);
        // }

        // asesmenMandiriRepository.save(am);
        // }
        // }

        for (String key : apl02Data.keySet()) {
            if (key.startsWith("kompeten_elemen_")) {

                // Parsing ID Elemen (Aman karena split index 2)
                // "kompeten" "elemen" "12"
                String[] parts = key.split("_");
                Long elemenId = Long.parseLong(parts[2]);
                String rekomendasi = apl02Data.get(key); // "K" atau "BK"

                AsesmenMandiri am = new AsesmenMandiri();
                am.setPermohonan(permohonan);

                // Set Elemen (Bukan KUK lagi)
                am.setUnitElemen(unitElemenSkemaRepository.findById(elemenId).orElse(null));

                am.setRekomendasiAsesi(rekomendasi);

                // Link ke Bukti Relevan
                // Format key bukti: "bukti_elemen_{elemenId}"
                String buktiKey = "bukti_elemen_" + elemenId;

                if (apl02Bukti.containsKey(buktiKey)) {
                    String[] buktiIds = apl02Bukti.get(buktiKey); // Dapat array dari select2
                    List<BuktiPortofolio> buktiList = new ArrayList<>();

                    if (buktiIds != null) {
                        for (String bId : buktiIds) {
                            // bId contoh: "portofolio_1"
                            if (portofolioMap.containsKey(bId)) {
                                buktiList.add(portofolioMap.get(bId));
                            }
                        }
                    }
                    am.setBuktiRelevan(buktiList);
                }

                asesmenMandiriRepository.save(am);
            }
        }
    }

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