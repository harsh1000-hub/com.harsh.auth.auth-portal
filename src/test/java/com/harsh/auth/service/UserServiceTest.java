package com.harsh.auth.service;

import com.harsh.auth.model.User;
import com.harsh.auth.model.VerificationToken;
import com.harsh.auth.repository.UserRepository;
import com.harsh.auth.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    VerificationTokenRepository tokenRepository;

    @Mock
    EmailService emailService;

    @Captor
    ArgumentCaptor<VerificationToken> tokenCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsEmailRegistered_true() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.email = email;

        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        assertTrue(userService.isEmailRegistered(email));
    }

    @Test
    public void testIsEmailRegistered_false() {
        when(userRepository.findByEmail("nope@example.com")).thenReturn(null);

        assertFalse(userService.isEmailRegistered("nope@example.com"));
    }

    @Test
    public void testRegisterUser() {
        String email = "user@test.com";
        String password = "secret123";

        User savedUser = userService.registerUser(email, password);

        assertNotNull(savedUser);
        assertEquals(email, savedUser.email);
        assertNotNull(savedUser.passwordHash);
        assertNotEquals(password, savedUser.passwordHash); // should be hashed

        verify(userRepository, times(1)).persist(any(User.class));
    }

    @Test
    public void testSendEmailVerificationToken() {
        User user = new User();
        user.email = "verify@test.com";

        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        String token = userService.sendEmailVerificationToken(user);

        assertNotNull(token);
        verify(tokenRepository, times(1)).persist(tokenCaptor.capture());
        verify(emailService, times(1)).sendVerificationEmail(eq(user.email), eq(token));

        VerificationToken savedToken = tokenCaptor.getValue();
        assertEquals(user, savedToken.user);
        assertEquals(token, savedToken.token);
        assertTrue(savedToken.expiryDate.isAfter(LocalDateTime.now()));
    }

    @Test
    public void testVerifyEmail_success() {
        String token = UUID.randomUUID().toString();
        User user = new User();
        user.email = "verify@test.com";
        user.isEmailVerified = false;

        VerificationToken vt = new VerificationToken(token, user, LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken(token)).thenReturn(vt);

        userService.verifyEmail(token);

        assertTrue(user.isEmailVerified);
        verify(userRepository, times(1)).persist(user);
        verify(tokenRepository, times(1)).delete(vt);
    }

    @Test
    public void testVerifyEmail_invalidToken() {
        when(tokenRepository.findByToken("bad-token")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.verifyEmail("bad-token")
        );

        assertEquals("Invalid or expired token", ex.getMessage());
    }

    @Test
    public void testVerifyEmail_expiredToken() {
        User user = new User();
        user.email = "expired@test.com";

        VerificationToken expired = new VerificationToken("expired-token", user, LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken("expired-token")).thenReturn(expired);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.verifyEmail("expired-token")
        );

        assertEquals("Invalid or expired token", ex.getMessage());
    }
}
