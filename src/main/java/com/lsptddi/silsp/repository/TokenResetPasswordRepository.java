// package com.lsptddi.silsp.repository;

// import com.lsptddi.silsp.model.TokenResetPassword;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import java.util.Optional;

// public class TokenResetPasswordRepository {

//     @Repository
//     public interface PasswordResetTokenRepository extends JpaRepository<TokenResetPassword, Long> {
//         Optional<TokenResetPassword> findByToken(String token);

//         void deleteByToken(String token); // Untuk hapus token setelah dipakai
//     }
// }

package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.TokenResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TokenResetPasswordRepository extends JpaRepository<TokenResetPassword, Long> {
    Optional<TokenResetPassword> findByToken(String token);
    void deleteByToken(String token); // Untuk hapus token setelah dipakai
}