package com.harsh.auth.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    public String email;

    @NotBlank
    @Column(nullable = false)
    public String passwordHash;

    @Column(nullable = false)
    public boolean isEmailVerified = false;

    public User() {}

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
