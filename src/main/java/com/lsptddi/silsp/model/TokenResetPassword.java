package com.lsptddi.silsp.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class TokenResetPassword {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_resetpassword")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public TokenResetPassword(String token, User user) {
        this.token = token;
        this.user = user;
        // Token berlaku selama 30 menit
        this.expiryDate = LocalDateTime.now().plusMinutes(2); 
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
}
