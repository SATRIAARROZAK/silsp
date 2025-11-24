package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Mencari role berdasarkan nama (misal: "ADMIN")
    Optional<Role> findByName(String name);
}