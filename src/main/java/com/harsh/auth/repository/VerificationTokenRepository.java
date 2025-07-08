package com.harsh.auth.repository;

import com.harsh.auth.model.VerificationToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VerificationTokenRepository implements PanacheRepository<VerificationToken> {
    public VerificationToken findByToken(String token) {
        return find("token", token).firstResult();
    }
}
