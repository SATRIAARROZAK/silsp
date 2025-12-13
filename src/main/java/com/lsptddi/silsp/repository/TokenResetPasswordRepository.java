package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.TokenResetPassword;
import com.lsptddi.silsp.model.User; // Pastikan import User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TokenResetPasswordRepository extends JpaRepository<TokenResetPassword, Long> {
    Optional<TokenResetPassword> findByToken(String token);
    
    // TAMBAHAN: Untuk mencari token berdasarkan User
    Optional<TokenResetPassword> findByUser(User user);
}