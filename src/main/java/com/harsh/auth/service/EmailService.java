package com.harsh.auth.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    public void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:8080/verify?token=" + token;
        String body = "Hi ,\n\nClick the link to verify your email:\n" + link + "\n\nRegards,\nAuth Portal";

        mailer.send(
                Mail.withText(to, "Verify your email", body)
        );
        System.out.println(" Email sent to: " + to + " with token: " + token);
    }
}
