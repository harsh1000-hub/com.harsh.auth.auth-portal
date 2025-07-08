package com.harsh.auth.controller;

import com.harsh.auth.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class VerificationResourceTest {

    @InjectMock
    UserService userService;

    @Test
    public void testValidToken() {
        // Arrange
        String token = "valid-token";
        doNothing().when(userService).verifyEmail(token);

        // Act
        Response response = io.restassured.RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("/verify");

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Email verified successfully"));

        verify(userService, times(1)).verifyEmail(token);
    }

    @Test
    public void testInvalidToken() {
        // Arrange
        String token = "invalid-token";
        doThrow(new RuntimeException("Invalid token")).when(userService).verifyEmail(token);

        // Act
        Response response = io.restassured.RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("/verify");

        // Assert
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Invalid token"));

        verify(userService, times(1)).verifyEmail(token);
    }
}
