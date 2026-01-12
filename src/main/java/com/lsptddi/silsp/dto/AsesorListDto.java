package com.lsptddi.silsp.dto;

import com.lsptddi.silsp.model.User;
import lombok.Data;

@Data
public class AsesorListDto {
    private User user;
    private Long jumlahAsesmen;

    // Constructor ini PENTING untuk JPQL
    public AsesorListDto(User user, Long jumlahAsesmen) {
        this.user = user;
        this.jumlahAsesmen = jumlahAsesmen;
    }
}