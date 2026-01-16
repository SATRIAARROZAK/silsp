package com.lsptddi.silsp.dto;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Data
public class SkemaDto {
    private Long id;
    // Tab 1
    // private String namaSkema;
    // private String kodeSkema;
    // private Integer level;
    // private String noSkkni;
    // private String tahunSkkni;
    // private Long jenisSkemaId; // ID dari RefSchemaType
    // private Long modeSkemaId; // ID dari RefSchemaMode
    // private LocalDate tanggalPenetapan;
    // private MultipartFile fileSkema;

    // --- TAB 1: DATA SKEMA ---
    private String namaSkema;
    private String kodeSkema;
    private Integer level;
    private String noSkkni;
    private String tahunSkkni;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalPenetapan;

    private Long jenisSkemaId;
    private Long modeSkemaId;
    private MultipartFile fileSkema;

    // // Tab 2 (Array dari form name="kodeUnit[]" dan "judulUnit[]")
    // private List<String> kodeUnit;
    // private List<String> judulUnit;

    // --- TAB 2: UNIT SKEMA (List Object) ---
    // Menggunakan inner class agar binding HTML lebih mudah (units[0].kodeUnit)
    private List<UnitItem> units;

    // --- TAB 3: ELEMEN (Referencing Unit Index) ---
    // elements[0].unitIndex = 0 (Menunjuk ke unit pertama di list units)
    private List<ElementItem> elements;

    // --- TAB 4: KUK (Referencing Element Index) ---
    // kuks[0].elementIndex = 0 (Menunjuk ke elemen pertama di list elements)
    private List<KukItem> kuks;

    // --- TAB 5: PERSYARATAN (String List Sederhana) ---
    private List<String> persyaratan;

    // ==========================================
    // INNER CLASSES (Untuk Data Binding)
    // ==========================================

    @Data
    public static class UnitItem {
        private String kodeUnit;
        private String judulUnit;
    }

    @Data
    public static class ElementItem {
        private Integer unitIndex; // Pointer ke index Unit
        private String noElemen;
        private String namaElemen;
    }

    @Data
    public static class KukItem {
        private Integer elementIndex; // Pointer ke index Element
        private String namaKuk;
    }
}