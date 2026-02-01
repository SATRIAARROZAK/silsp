package com.lsptddi.silsp.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    @JsonProperty("isRead") // Paksa nama JSON jadi "isRead"
    private boolean isRead;
    private String url;
    private LocalDateTime time; // Waktu asli
    private String formattedTime; // Waktu string cantik

    // Helper Constructor/Setter
    public void setTimeAndFormat(LocalDateTime time) {
        this.time = time;

        LocalDateTime now = LocalDateTime.now();
        if (time.toLocalDate().isEqual(now.toLocalDate())) {
            // Hari ini: Tampilkan Jam (14:30)
            this.formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            // Hari lain: Tampilkan Tanggal (20 Januari 2026 14:30)
            this.formattedTime = time.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", new Locale("id", "ID")));
        }
    }
}