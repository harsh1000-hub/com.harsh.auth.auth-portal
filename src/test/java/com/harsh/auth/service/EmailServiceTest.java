package com.harsh.auth.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    Mailer mailer;

    @Captor
    ArgumentCaptor<Mail> mailCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendVerificationEmail() {
        // Arrange
        String recipient = "test@example.com";
        String token = "abc123";

        // Act
        emailService.sendVerificationEmail(recipient, token);

        // Assert
        verify(mailer, times(1)).send(mailCaptor.capture());
        Mail sentMail = mailCaptor.getValue();

        // Verify Mail content
        assert sentMail != null;
        assert sentMail.getTo().contains(recipient);
        assert sentMail.getSubject().equals("Verify your email");
        assert sentMail.getText().contains("http://localhost:8080/verify?token=" + token);
        assert sentMail.getText().contains("Click the link to verify your email");
    }
}
