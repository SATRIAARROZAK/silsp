package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE " +
            "(:role IS NULL OR :role = '' OR r.name = :role) AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") String role);

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
    Optional<User> findByEmail(String email);

    /**
     * Mengecek apakah username sudah ada di database?
     * Return true jika ada, false jika belum.
     */
    boolean existsByUsername(String username);

    /**
     * Mengecek apakah email sudah ada di database?
     */
    boolean existsByEmail(String email);
}