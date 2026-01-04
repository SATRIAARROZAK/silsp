package com.lsptddi.silsp.repository;

import java.util.List;
import com.lsptddi.silsp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.lsptddi.silsp.dto.UserRoleDto; // Import DTO baru
// import java.util.List;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        @Query("SELECT new com.lsptddi.silsp.dto.UserRoleDto(u, r) " +
                        "FROM User u JOIN u.roles r WHERE " +
                        "(:role IS NULL OR :role = '' OR r.name = :role) AND " +
                        "(:keyword IS NULL OR :keyword = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<UserRoleDto> searchUserRoles(@Param("keyword") String keyword,
                        @Param("role") String role,
                        Pageable pageable);

        // @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE " +
        // "(:role IS NULL OR :role = '' OR r.name = :role) AND " +
        // "(:keyword IS NULL OR :keyword = '' OR LOWER(u.fullName) LIKE
        // LOWER(CONCAT('%', :keyword, '%')))")
        // Page<User> searchUsers(@Param("keyword") String keyword, @Param("role")
        // String role, Pageable pageable);

        // 1. METHOD BAWAAN (Otomatis ada, tidak perlu ditulis):
        // - findAll() -> Mengambil semua user
        // - findById(id) -> Mencari user berdasarkan ID
        // - save(user) -> Menyimpan/Update user
        // - deleteById(id) -> Menghapus user

        // 2. CUSTOM METHOD (Tambahan yang sering dibutuhkan):

        /**
         * Mencari user berdasarkan username.
         * Penting untuk fitur LOGIN nanti (Spring Security).
         */
        Optional<User> findByUsername(String username);

        /**
         * Mencari user berdasarkan email.
         * Berguna untuk validasi agar tidak ada email kembar saat registrasi.
         */

        List<User> findByRolesName(String roleName);

        Optional<User> findByEmail(String email);

        Optional<User> findByUsernameOrEmail(String username, String email);

        /**
         * Mengecek apakah username sudah ada di database?
         * Return true jika ada, false jika belum.
         */
        // Cek Username untuk ADD (Baru)
        boolean existsByUsername(String username);

        // Cek Email untuk ADD (Baru)
        boolean existsByEmail(String email);

        boolean existsByNik(String nik);

        // boolean existsByNik(String nik);

        // Cek Username untuk EDIT (Kecuali ID sendiri)
        // "Cari user lain yang punya username ini, tapi ID-nya bukan ID saya"
        boolean existsByUsernameAndIdNot(String username, Long id);

        // Cek Email untuk EDIT (Kecuali ID sendiri)
        boolean existsByEmailAndIdNot(String email, Long id);

}