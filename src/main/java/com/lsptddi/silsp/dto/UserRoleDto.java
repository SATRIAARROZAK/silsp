package com.lsptddi.silsp.dto;

import com.lsptddi.silsp.model.Role;
import com.lsptddi.silsp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoleDto {
    private User user;
    private Role role;
}