package com.harsh.auth.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false)
    public LocalDateTime expiryDate;

    public VerificationToken() {}

    public VerificationToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
