package com.harsh.auth.controller;

import com.harsh.auth.model.User;
import com.harsh.auth.service.UserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class SignupResourceTest {

    @InjectMock
    UserService userService;

    @Test
    public void testSignupSuccess() {
        String testEmail = "test@example.com";
        String testPassword = "secret123";
        String expectedToken = "mock-token-123";

        User mockUser = new User();
        mockUser.email = testEmail;

        when(userService.isEmailRegistered(testEmail)).thenReturn(false);
        when(userService.registerUser(eq(testEmail), eq(testPassword))).thenReturn(mockUser);
        when(userService.sendEmailVerificationToken(mockUser)).thenReturn(expectedToken);

        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"" + testEmail + "\", \"password\": \"" + testPassword + "\" }")
                .when()
                .post("/signup");

        // ✅ Asserts
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("Signup successful!", response.jsonPath().getString("message"));
        assertEquals(expectedToken, response.jsonPath().getString("token"));

        verify(userService, times(1)).isEmailRegistered(testEmail);
        verify(userService, times(1)).registerUser(testEmail, testPassword);
        verify(userService, times(1)).sendEmailVerificationToken(mockUser);
    }

    @Test
    public void testSignupMissingFields() {
        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"\", \"password\": \"\" }")
                .when()
                .post("/signup");

        assertEquals(400, response.getStatusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("must not be blank"));
        assertTrue(responseBody.contains("email"));
        assertTrue(responseBody.contains("password"));
    }



    @Test
    public void testSignupDuplicateEmail() {
        String email = "harsh@example.com";

        when(userService.isEmailRegistered(email)).thenReturn(true);

        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"" + email + "\", \"password\": \"12345678\" }")
                .when()
                .post("/signup");

        assertEquals(409, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Email already registered"));
    }

}