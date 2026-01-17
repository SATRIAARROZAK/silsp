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

 

    // Tab 2, 3, 4 (DATA JSON - PENGGANTI LIST ARRAY)
    private String unitsJson; // Tab 2: Unit
    private String elementsJson; // Tab 3: Elemen
    private String kuksJson; // Tab 4: KUK (BARU)
    private String requirementsJson;// Tab 5: Persyaratan

    // Field helper lama (bisa diabaikan/dihapus jika sudah full JSON)
    private List<String> kodeUnit;
    private List<String> judulUnit;
    private List<String> persyaratan;

}