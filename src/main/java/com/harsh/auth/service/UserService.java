package com.harsh.auth.service;

import com.harsh.auth.model.User;
import com.harsh.auth.model.VerificationToken;
import com.harsh.auth.repository.UserRepository;
import com.harsh.auth.repository.VerificationTokenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    VerificationTokenRepository tokenRepository;

    @Inject
    EmailService emailService;

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Transactional
    public User registerUser(String email, String plainPassword) {
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        User user = new User(email, hashedPassword);
        userRepository.persist(user);
        return user;
    }

    @Transactional
    public String sendEmailVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(30)
        );
        tokenRepository.persist(vt);

        // Optional: call to email service (you can remove if unused)
        emailService.sendVerificationEmail(user.email, token);
        return token;
    }


    @Transactional
    public void verifyEmail(String token) {
        VerificationToken vt = tokenRepository.findByToken(token);

        if (vt == null || vt.isExpired()) {
            throw new RuntimeException("Invalid or expired token");
        }
        vt.user.isEmailVerified = true;
        userRepository.persist(vt.user);
        tokenRepository.delete(vt);
    }


//    public <T> void registerUser(T any) {
//    }
}
