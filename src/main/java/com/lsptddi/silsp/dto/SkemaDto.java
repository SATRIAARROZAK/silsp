package com.lsptddi.silsp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Data
public class SkemaDto {
    private Long id;
    // Tab 1
    private String namaSkema;
    private String kodeSkema;
    private Integer level;
    private String noSkkni;
    private String tahunSkkni;
    private Long jenisSkemaId; // ID dari RefSchemaType
    private Long modeSkemaId; // ID dari RefSchemaMode
    private LocalDate tanggalPenetapan;
    private MultipartFile fileSkema;

    // // Tab 2 (Array dari form name="kodeUnit[]" dan "judulUnit[]")
    private List<String> kodeUnit;
    private List<String> judulUnit;

    // -- TAMBAHAN TAB 3 (ELEMEN) ---
    // elemenKodeUnitRef: Menyimpan 'kodeUnit' yang dipilih di dropdown Tab 3
    // private List<String> elemenKodeUnitRef; 
    // private List<String> noElemen;
    // private List<String> namaElemen;

    // --- TAB 2, 3, 4 (DATA JSON - PENGGANTI LIST ARRAY) ---
    // Data ini dikirim sebagai String JSON dari JavaScript
    private String unitsJson;       // Berisi: [{code: "...", title: "..."}, ...]
    private String elementsJson;    // Berisi: [{unitRef: "...", no: "...", name: "..."}, ...]
    private String requirementsJson;// Berisi: ["...", "..."]

    // --- TAB 5: PERSYARATAN (String List Sederhana) ---
    private List<String> persyaratan;

}