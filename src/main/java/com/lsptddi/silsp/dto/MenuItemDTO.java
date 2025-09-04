package com.lsptddi.silsp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuItemDTO {
    private String label;
    private String url;
    private String icon;
    
}
