package com.harsh.auth.controller;

import com.harsh.auth.dto.LoginRequest;
import com.harsh.auth.model.User;
import com.harsh.auth.repository.UserRepository;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.HttpServerResponse;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginResourceTest {

    @InjectMocks
    LoginResource loginResource;

    @Mock
    UserRepository userRepo;

    @Mock
    RoutingContext ctx;

    @Mock
    HttpServerResponse serverResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(ctx.response()).thenReturn(serverResponse);
    }

    private LoginRequest buildRequest(String email, String password) {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    @Test
    void testInvalidInput_emptyEmail() {
        LoginRequest request = buildRequest("", "password123");

        Response response = loginResource.login(request, ctx);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Invalid input", response.getEntity());
    }

    @Test
    void testInvalidCredentials_userNotFound() {
        LoginRequest request = buildRequest("test@example.com", "password123");

        when(userRepo.findByEmail("test@example.com")).thenReturn(null);

        Response response = loginResource.login(request, ctx);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Invalid email or password", response.getEntity());
    }

    @Test
    void testInvalidCredentials_wrongPassword() {
        LoginRequest request = buildRequest("test@example.com", "wrongpassword");

        User user = new User();
        user.id = 1L;
        user.email = "test@example.com";
        user.passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw("correctpassword", org.mindrot.jbcrypt.BCrypt.gensalt());
        user.isEmailVerified = true;

        when(userRepo.findByEmail("test@example.com")).thenReturn(user);

        Response response = loginResource.login(request, ctx);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Invalid email or password", response.getEntity());
    }

    @Test
    void testLogin_emailNotVerified() {
        LoginRequest request = buildRequest("test@example.com", "correctpassword");

        User user = new User();
        user.id = 1L;
        user.email = "test@example.com";
        user.passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw("correctpassword", org.mindrot.jbcrypt.BCrypt.gensalt());
        user.isEmailVerified = false;

        when(userRepo.findByEmail("test@example.com")).thenReturn(user);

        Response response = loginResource.login(request, ctx);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("You need to validate your email to access the portal", response.getEntity());
    }

    @Test
    void testLogin_success() {
        LoginRequest request = buildRequest("test@example.com", "correctpassword");

        User user = new User();
        user.id = 1L;
        user.email = "test@example.com";
        user.passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw("correctpassword", org.mindrot.jbcrypt.BCrypt.gensalt());
        user.isEmailVerified = true;

        when(userRepo.findByEmail("test@example.com")).thenReturn(user);

        Response response = loginResource.login(request, ctx);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Your email is validated. You can access the portal", response.getEntity());

        verify(serverResponse, times(1)).addCookie(any(Cookie.class));
    }
}
