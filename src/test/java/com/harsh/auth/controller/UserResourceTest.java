package com.harsh.auth.controller;

import com.harsh.auth.model.User;
import com.harsh.auth.repository.UserRepository;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    @InjectMocks
    UserResource userResource;

    @Mock
    UserRepository userRepo;

    @Mock
    RoutingContext ctx;

    @Mock
    HttpServerRequest request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(ctx.request()).thenReturn(request);
    }

    @Test
    public void testMissingCookie() {
        when(request.getCookie("userId")).thenReturn(null);

        Response response = userResource.getProfile(ctx);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("You are not logged in"));
    }

    @Test
    public void testEmptyCookieValue() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("");
        when(request.getCookie("userId")).thenReturn(cookie);

        Response response = userResource.getProfile(ctx);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("You are not logged in"));
    }

    @Test
    public void testInvalidCookieValue() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("invalid");
        when(request.getCookie("userId")).thenReturn(cookie);

        Response response = userResource.getProfile(ctx);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid session"));
    }

    @Test
    public void testUserNotFound() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("42");
        when(request.getCookie("userId")).thenReturn(cookie);

        when(userRepo.findById(42L)).thenReturn(null);

        Response response = userResource.getProfile(ctx);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("User not found"));
    }

    @Test
    public void testSuccess() {
        Cookie cookie = mock(Cookie.class);
        when(cookie.getValue()).thenReturn("1");
        when(request.getCookie("userId")).thenReturn(cookie);

        User user = new User();
        user.id = 1L;
        user.email = "test@example.com";

        when(userRepo.findById(1L)).thenReturn(user);

        Response response = userResource.getProfile(ctx);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("test@example.com"));
    }
}
