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

    // --- TAB 5: PERSYARATAN (String List Sederhana) ---
    private List<String> persyaratan;

}