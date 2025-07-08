package com.harsh.auth.controller;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogoutResourceTest {

    @InjectMocks
    LogoutResource logoutResource;

    @Mock
    RoutingContext routingContext;

    @Mock
    HttpServerResponse serverResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(routingContext.response()).thenReturn(serverResponse);
        when(serverResponse.addCookie(any(Cookie.class))).thenReturn(serverResponse);
    }

    @Test
    public void testLogoutShouldExpireCookie() {
        // Act
        Response response = logoutResource.logout(routingContext);

        // Assert
        assertEquals(200, response.getStatus());

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(serverResponse, times(1)).addCookie(cookieCaptor.capture());

        Cookie expiredCookie = cookieCaptor.getValue();
        assertEquals("userId", expiredCookie.getName());
        assertEquals("", expiredCookie.getValue());
        assertEquals(0, expiredCookie.getMaxAge());
        assertEquals("/", expiredCookie.getPath());
    }
}
