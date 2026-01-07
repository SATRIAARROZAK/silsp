package com.lsptddi.silsp.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleDto {
    private Long id;
    private String name;
    private String code; // Auto
    private String bnspCode;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    private Integer quota;
    private Long budgetSource;   // Dari API
    private Long budgetProvider; // Dari API

    private Long tukId;

    // List ID untuk relasi Many-to-Many
    private List<Long> assessorIds;
    private List<Long> schemaIds;

    
}